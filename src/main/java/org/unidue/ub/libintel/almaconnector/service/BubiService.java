package org.unidue.ub.libintel.almaconnector.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLibrary;
import org.unidue.ub.alma.shared.bibs.HoldingDataTempLocation;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceServices;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class BubiService {

    @Value("${libintel.bubi.journal.fund:55510-0-1100}")
    private String journalFund;

    @Value("${libintel.bubi.monograph.fund:55510-0-1200}")
    private String monographFund;

    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final CoreDataRepository coreDataRepository;

    private final PrimoService primoService;

    private final BubiDataRepository bubiDataRepository;

    private final FileWriterService.AlmaItemService almaItemService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaInvoiceServices almaInvoiceServices;

    private final Logger log = LoggerFactory.getLogger(BubiService.class);

    public BubiService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            CoreDataRepository coreDataRepository,
            BubiDataRepository bubiDataRepository,
            PrimoService primoService,
            FileWriterService.AlmaItemService almaItemService,
            AlmaPoLineService almaPoLineService,
            AlmaInvoiceServices almaInvoiceService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.coreDataRepository = coreDataRepository;
        this.bubiDataRepository = bubiDataRepository;
        this.primoService = primoService;
        this.almaItemService = almaItemService;
        this.almaPoLineService = almaPoLineService;
        this.almaInvoiceServices = almaInvoiceService;
    }

    public List<BubiOrder> getBubiOrders(String mode) {
        switch (mode) {
            case "all": return this.bubiOrderRepository.findAll();
            case "sent": return this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.SENT);
            case "complaint": return this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.COMPLAINT);
            default: {
                List<BubiOrder> activeOrders = new ArrayList<>();
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.NEW));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.SENT));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.WAITING));
                activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.COMPLAINT));
                return activeOrders;
            }
        }
    }

    public List<BubiOrderLine> getAllBubiOrderLinesForBubi(String vendorId) {
        return this.bubiOrderLineRepository.findAllByVendorId(vendorId);
    }

    public List<CoreData> getActiveCoreData() {
        return this.coreDataRepository.findAllByActive(true);
    }

    public List<CoreData> getAllCoreData() {
        return this.coreDataRepository.findAll();
    }

    public CoreData saveCoreData(CoreData coreData) {
        return this.coreDataRepository.save(coreData);
    }


    public BubiOrderLine getBubiOrderLineFromBarcode(String barcode) {
        if (barcode != null) {
            Item item = this.almaItemService.findItemByBarcode(barcode);
            return expandBubiOrderLineFromItem(item);
        }
        return null;
    }

    public BubiOrderLine expandBubiOrderLineFromShelfmark(String collection, String shelfmark) {
        if (shelfmark != null && collection != null) {
            return retrieveBubiOrderLine(collection.toUpperCase(), shelfmark.toUpperCase());
        }
        return null;
    }

    public BubiOrderLine expandBubiOrderLineFromItem(Item item) {
        String collection = item.getItemData().getLocation().getDesc().toUpperCase(Locale.ROOT);
        String shelfmark = item.getHoldingData().getCallNumber().toUpperCase(Locale.ROOT);
        String campus = "E0001";
        try {
            campus = item.getItemData().getLibrary().getValue().toUpperCase(Locale.ROOT);
        } catch (Exception e) {
            if (collection.startsWith("D"))
                campus = "D0001";
        }
        String material = "book";
        if ("ISSBD".equals(item.getItemData().getPhysicalMaterialType().getValue()))
            material = "journal";
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        CoreData coredata = this.coreDataRepository.findAllByCollectionAndShelfmark(collection, shelfmark);
        if (coredata == null) {
            log.info(String.format("no core data available - applying standard values for campus %s and material type %s", campus, material));
            coredata = this.coreDataRepository.findCoreDataByActiveAndShelfmarkAndMediaType(true, "STANDARD_" + campus, material);
            bubiOrderLine.setTitle(item.getBibData().getTitle());
            bubiOrderLine.setAlmaMmsId(item.getBibData().getMmsId());
            bubiOrderLine.setAlmaHoldingId(item.getHoldingData().getHoldingId());
            bubiOrderLine.setAlmaItemId(item.getItemData().getPid());
            bubiOrderLine.addCoreData(coredata, true);
        } else {
            log.info("found core data");
            bubiOrderLine.addCoreData(coredata, false);
        }
        setFundAndPrice(bubiOrderLine);
        return bubiOrderLine;
    }

    public BubiOrderLine saveBubiOrderLine(BubiOrderLine bubiOrderLine) {
        bubiOrderLine.setLastChange(new Date());
        return this.bubiOrderLineRepository.save(bubiOrderLine);
    }

    public List<BubiOrderLine> getOrderLines(String mode) {
        switch (mode) {
            case "all": return this.bubiOrderLineRepository.findAll();
            case "packed": return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.PACKED);
            case "sent": return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.SENT);
            case "waiting": return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING);
            default: return getActiveOrderlines();
        }
    }

    public List<BubiOrderLine> getActiveOrderlines() {
        List<BubiOrderLine> allOpenOrderlines = new ArrayList<>();
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.NEW));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING));
        return allOpenOrderlines;
    }

    public BubiOrderLine getBubiOrderLineFromIdentifier(String identifier) {
        return this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineId(identifier);
    }

    public CoreDataImportRun readCoreDataFromExcelSheet(CoreDataImportRun coreDataImportRun, XSSFWorkbook workbook) {
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            CoreData coreData = new CoreData();
            if (row.getCell(1) == null || row.getCell(0) == null)
                continue;
            String collection = row.getCell(0).getStringCellValue();
            String shelfmark = row.getCell(1).getStringCellValue();
            String journalRegEx = "\\d\\dZ\\d+.*";
            if (Pattern.matches(journalRegEx, shelfmark)) {
                shelfmark = shelfmark.replace("Z", " Z ");
            }
            coreData.setCollection(collection);
            coreData.setShelfmark(shelfmark);
            try {
                coreData.setTitle(row.getCell(2).getStringCellValue());
            } catch (Exception e) {
                coreData.setTitle("");
            }
            try {
                coreData.setMinting(row.getCell(5).getStringCellValue());
            } catch (Exception e) {
                coreData.setMinting("");
            }
            try {
                coreData.setPart(row.getCell(6).getStringCellValue());
            } catch (Exception e) {
                coreData.setPart("");
            }
            try {
                coreData.setColor(row.getCell(7).getStringCellValue());
            } catch (Exception e) {
                coreData.setColor("");
            }
            try {
                coreData.setCover(row.getCell(8).getStringCellValue());
            } catch (Exception e) {
                coreData.setCover("");
            }
            try {
                coreData.setBinding(row.getCell(9).getStringCellValue());
            } catch (Exception e) {
                coreData.setBinding("");
            }
            try {
                coreData.setVendorId(row.getCell(11).getStringCellValue());
            } catch (Exception e) {
                coreData.setVendorId("");
            }
            try {
                coreData.setVolume(row.getCell(13).getStringCellValue());
            } catch (Exception e) {
                coreData.setVolume("");
            }
            try {
                coreData.setIssue(row.getCell(15).getStringCellValue());
            } catch (Exception e) {
                coreData.setIssue("");
            }
            try {
                coreData.setYear(row.getCell(14).getStringCellValue());
            } catch (Exception e) {
                coreData.setYear("");
            }
            try {
                String comment = row.getCell(39).getStringCellValue();
                coreData.setComment(comment);
                coreData.setActive(!comment.contains("abbest."));
            } catch (Exception e) {
                coreData.setComment("");
            }
            try {
                if ("j".equals(row.getCell(43).getStringCellValue()))
                    coreData.setMediaType("journal");
                else
                    coreData.setMediaType("book");
            } catch (Exception e) {
                coreData.setMediaType("journal");
            }
            try {
                coreData.setBindingsFollow(row.getCell(44).getStringCellValue());
            } catch (Exception e) {
                coreData.setBindingsFollow("");
            }
            try {
                coreData.setAlternativeBubiData(row.getCell(45).getStringCellValue());
            } catch (Exception e) {
                coreData.setAlternativeBubiData("");
            }
            coreDataImportRun.addCoreData(coreData);
            if (coreData.isActive()) {
                AlmaItemData almaItemData = new AlmaItemData(coreData.getCollection(), coreData.getShelfmark());
                List<AlmaItemData> foundData = this.primoService.getPrimoResponse(almaItemData);
                if (foundData.size() == 1) {
                    coreData.setAlmaMmsId(foundData.get(0).mmsId);
                    coreData.setAlmaHoldingId(foundData.get(0).holdingId);
                    coreData.setTitle(foundData.get(0).title);
                } else if (foundData.size() > 1) {
                    for (AlmaItemData foundDatum : foundData) {
                        if (coreData.getTitle().equals(foundDatum.title)) {
                            coreData.setAlmaMmsId(foundData.get(0).mmsId);
                            coreData.setAlmaHoldingId(foundData.get(0).holdingId);
                            coreData.setTitle(foundData.get(0).title);
                        }
                    }
                } else
                    coreData.setActive(false);
            }
            this.coreDataRepository.save(coreData);
        }
        return coreDataImportRun;
    }

    public List<BubiData> listAllBubiData() {
        return this.bubiDataRepository.findAll();
    }

    public BubiData getVendorAccount(String vendorID, String collection) {
        String campus = collection.startsWith("E") ? "E0001" : "D0001";
        List<BubiData> bubiData = this.bubiDataRepository.findByVendorIdAndCampus(vendorID, campus);
        if (bubiData.size() == 0)
            return new BubiData();
        else
            return bubiData.get(0);
    }

    private boolean addCoreData(BubiOrderLine bubiOrderLine) {
        CoreData coredata = this.coreDataRepository.findAllByCollectionAndShelfmark(bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark());
        if (coredata != null) {
            bubiOrderLine.addCoreData(coredata, false);
            return true;
        }
        return false;
    }

    private void setFundAndPrice(BubiOrderLine bubiOrderline) {
        BubiData bubiData = getVendorAccount(bubiOrderline.getVendorId(), bubiOrderline.getCollection());
        bubiOrderline.setVendorAccount(bubiData.getVendorAccount());
        if (bubiOrderline.getShelfmark().contains(" Z ")) {
            bubiOrderline.setFund(journalFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceJournal());
        } else if (bubiOrderline.getStandard()) {
            bubiOrderline.setFund(journalFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        } else {
            bubiOrderline.setFund(monographFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        }
    }

    public List<BubiOrder> packBubiOrder(BubiOrder bubiOrder) {
        Hashtable<String, BubiOrder> bubiOrders = new Hashtable<>();
        for (int i = 0; i < bubiOrder.getBubiOrderLines().size(); i++) {
            BubiOrderLine bubiOrderLine = bubiOrder.getBubiOrderLines().get(i);

            PoLine poLine = buildPoLine(bubiOrderLine);
            poLine = almaPoLineService.savePoLine(poLine);
            bubiOrderLine.setAlmaPoLineId(poLine.getNumber());
            bubiOrderLine.setPositionalNumber(i + 1);
            bubiOrderLine.setStatus(BubiStatus.PACKED);
            bubiOrderLine.setLastChange(new Date());
            String key = bubiOrderLine.getVendorId() + "-" + bubiOrderLine.getVendorAccount();
            BubiOrder bubiOrderInd;
            if (bubiOrders.containsKey(key)) {
                bubiOrderInd = bubiOrders.get(key);
            } else {
                long counter = this.bubiOrderRepository.countAllByVendorIdAndVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getVendorAccount()) + 1;
                bubiOrderInd = new BubiOrder(bubiOrderLine.getVendorId(), bubiOrderLine.getVendorAccount(), counter);
                bubiOrders.put(key, bubiOrderInd);
            }
            bubiOrderInd.addBubiOrderLine(bubiOrderLine);
            bubiOrderLine.setBubiOrder(bubiOrderInd);
            bubiOrderInd.calculateTotalPrice();
            setTemporaryLocation(bubiOrderLine);

            this.bubiOrderRepository.save(bubiOrderInd);
            this.bubiOrderLineRepository.save(bubiOrderLine);
        }
        return new ArrayList<>(bubiOrders.values());
    }

    private void setTemporaryLocation(BubiOrderLine bubiOrderLine) {
        Item item = almaItemService.findItemByMmsAndItemId(bubiOrderLine.getAlmaMmsId(), bubiOrderLine.getAlmaItemId());
        item.getHoldingData().setInTempLocation(true);
        item.getItemData().setPublicNote("Buchbinder");
        switch(item.getItemData().getLibrary().getValue()) {
            case "E0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("EBB"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("E0001"));
                break;
            }
            case "D0001": {
                item.getHoldingData().tempLocation(new HoldingDataTempLocation().value("DBB"));
                item.getHoldingData().tempLibrary(new HoldingDataTempLibrary().value("D0001"));
                break;
            }
        }
    }

    public BubiOrder payBubiOrder(BubiOrder bubiOrder) {
        Invoice invoice = getInvoiceForBubiOrder(bubiOrder);
        invoice = this.almaInvoiceServices.saveInvoice(invoice);
        List<InvoiceLine> invoiceLines = getInvoiceLinesForBubiOrder(bubiOrder);
        for (InvoiceLine invoiceLine : invoiceLines)
            this.almaInvoiceServices.addInvoiceLine(invoice.getId(), invoiceLine);
        this.almaInvoiceServices.processInvoice(invoice.getId());
        bubiOrder.setPaymentStatus(PaymentStatus.PAID);
        this.bubiOrderRepository.save(bubiOrder);
        return bubiOrder;
    }

    private BubiOrderLine retrieveBubiOrderLine(String collection, String shelfmark) {
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        boolean isDataAvailabe = addCoreData(bubiOrderLine);
        if (!isDataAvailabe) {
            AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
            List<AlmaItemData> foundAlmaItemData = this.primoService.getPrimoResponse(almaItemData);
            if (foundAlmaItemData.size() > 0) {
                bubiOrderLine.addAlmaItemData(foundAlmaItemData.get(0));
            }
        }
        setFundAndPrice(bubiOrderLine);
        return bubiOrderLine;
    }

    /**
     * creates an Alma PO Line form the bubi order line
     * @param bubiOrderLine the bubi order line from which the Alma PO Line is created
     * @return an Alma PoLine object
     */
    public static PoLine buildPoLine(BubiOrderLine bubiOrderLine) {
        PoLineOwner poLineOwner;

        // set the owner depending on the collection
        if (bubiOrderLine.getCollection().startsWith("D"))
            poLineOwner = new PoLineOwner().value("D0001");
        else if (bubiOrderLine.getCollection().startsWith("E5"))
            poLineOwner = new PoLineOwner().value("E0023");
        else
            poLineOwner = new PoLineOwner().value("E0001");

        // creates the amount and fund information
        Amount amount = new Amount().sum(String.valueOf(bubiOrderLine.getPrice()))
                .currency(new AmountCurrency().value("EUR"));
        FundDistributionPoLine fundDistribution = new FundDistributionPoLine()
                .fundCode(new FundDistributionFundCode().value(bubiOrderLine.getFund()))
                .amount(amount);
        List<FundDistributionPoLine> fundList = new ArrayList<>();
        fundList.add(fundDistribution);

        // creates the resource metadata
        ResourceMetadata resourceMetadata = new ResourceMetadata()
                .mmsId(new ResourceMetadataMmsId().value(bubiOrderLine.getAlmaMmsId()))
                .title(bubiOrderLine.getTitle());

        // sets the status to a auto packaging
        PoLineStatus status = new PoLineStatus().value("AUTO_PACKAGING").desc("Auto Packaging");
        return new PoLine()
                .vendorReferenceNumber(String.format("%s - %S:%s)", bubiOrderLine.getFund(),
                        bubiOrderLine.getCollection(),
                        bubiOrderLine.getShelfmark()))
                .sourceType(new PoLineSourceType().value("MANUALENTRY"))
                .type(new PoLineType().value("OTHER_SERVICES_OT"))
                .status(status)
                .price(amount)
                .baseStatus(PoLine.BaseStatusEnum.ACTIVE)
                .owner(poLineOwner)
                .resourceMetadata(resourceMetadata)
                .vendor(new PoLineVendor().value(bubiOrderLine.getVendorId()))
                .vendorAccount(bubiOrderLine.getVendorAccount())
                .fundDistribution(fundList);
    }

    /**
     * creates an invoice for a bubi order.
     * @param bubiOrder a bubi order
     * @return an Alma Invoice object
     */
    public static Invoice getInvoiceForBubiOrder(BubiOrder bubiOrder) {
        // create new Invocie
        Invoice invoice = new Invoice();

        // set the vendor information with the information from the bubi order
        invoice.vendor(new InvoiceVendor().value(bubiOrder.getVendorId()))
                .vendorAccount(bubiOrder.getVendorAccount());

        // set total amount and payment method
        invoice.totalAmount(bubiOrder.getTotalAmount());
        invoice.paymentMethod(new InvoicePaymentMethod().value("ACCOUNTINGDEPARTMENT"));

        // set the status information
        invoice.invoiceStatus(new InvoiceInvoiceStatus().value("ACTIVE"));


        // set the VAT information
        invoice.invoiceVat(new InvoiceVat().vatPerInvoiceLine(true).type(new InvoiceVatType().value("INCLUSIVE")));

        // set the invoice number and date
        invoice.setNumber(bubiOrder.getInvoiceNumber());
        invoice.setInvoiceDate(bubiOrder.getInvoiceDate());

        // set the owner of the order line
        if (bubiOrder.getBubiOrderLines().get(0).getCollection().startsWith("D"))
            invoice.setOwner(new InvoiceOwner().value("D0001"));
        else
            invoice.setOwner(new InvoiceOwner().value("E0001"));
        return invoice;
    }

    /**
     * creates the individual invoice lines for the bubi order lines in the bubi order
     * @param bubiOrder the bubi order holding the individual bubi order lines
     * @return a list of Alma InvoiceLine-objects
     */
    public static List<InvoiceLine> getInvoiceLinesForBubiOrder(BubiOrder bubiOrder) {
        // create new list of order lines
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        for (int i = 0; i< bubiOrder.getBubiOrderLines().size(); i++) {
            // retrieve the bubi order line
            BubiOrderLine bubiOrderLine = bubiOrder.getBubiOrderLines().get(i);

            // set the standard value for the VAT
            InvoiceLineVat invoiceLineVat = new InvoiceLineVat().vatCode(new InvoiceLineVatVatCode().value("H8"));

            // set the fund distribution
            FundDistributionFundCode fundDistributionFundCode = new FundDistributionFundCode().value(bubiOrderLine.getFund());
            FundDistribution fundDistribution = new FundDistribution().fundCode(fundDistributionFundCode).amount(bubiOrderLine.getPrice());
            List<FundDistribution> fundDistributionList = new ArrayList<>();
            fundDistributionList.add(fundDistribution);

            // create invoice line with all information and add it to the list
            InvoiceLine invoiceLine = new InvoiceLine()
                    .poLine(bubiOrderLine.getAlmaPoLineId())
                    .fullyInvoiced(true)
                    .totalPrice(bubiOrderLine.getPrice())
                    .invoiceLineVat(invoiceLineVat)
                    .fundDistribution(fundDistributionList);
            invoiceLines.add(invoiceLine);
        }
        return invoiceLines;
    }

}

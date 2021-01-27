package org.unidue.ub.libintel.almaconnector.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.acq.PoLineStatus;
import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiDataRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.buildPoLine;
import static org.unidue.ub.libintel.almaconnector.Utils.getInvoiceForBubiOrder;

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

    private final VendorService vendorService;

    private final BubiDataRepository bubiDataRepository;

    private final ItemService itemService;

    private final AlmaPoLineService almaPoLineService;

    private final AlmaInvoiceServices almaInvoiceServices;

    private final Logger log = LoggerFactory.getLogger(BubiService.class);

    public BubiService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            CoreDataRepository coreDataRepository,
            BubiDataRepository bubiDataRepository,
            PrimoService primoService,
            VendorService vendorService,
            ItemService itemService,
            AlmaPoLineService almaPoLineService,
            AlmaInvoiceServices almaInvoiceService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.coreDataRepository = coreDataRepository;
        this.bubiDataRepository = bubiDataRepository;
        this.primoService = primoService;
        this.itemService = itemService;
        this.vendorService = vendorService;
        this.almaPoLineService = almaPoLineService;
        this.almaInvoiceServices = almaInvoiceService;
    }

    public BubiOrder getBubiOrders(String orderNumber) {
        return this.bubiOrderRepository.getOne(orderNumber);
    }

    public List<BubiOrder> getAllBubiOrder() { return this.bubiOrderRepository.findAll(); }

    public List<BubiOrder> getActiveBubiOrder() {
        List<BubiOrder> activeOrders = new ArrayList<>();
        activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.NEW));
        activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.SENT));
        activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.WAITING));
        activeOrders.addAll(this.bubiOrderRepository.findAllByBubiStatus(BubiStatus.COMPLAINT));
        return this.bubiOrderRepository.findAll(); }

    public List<BubiOrderLine> getAllBubiOrderLines() {
        return this.bubiOrderLineRepository.findAll();
    }

    public List<BubiOrderLine> getAllBubiOrderLinesForBubi(String vendorId) {
        return this.bubiOrderLineRepository.findAllByVendorId(vendorId);
    }

    public List<BubiOrderLine> getAllBubiOrderLinesForVendorAccount(String vendorId, String vendorAccount) {
        return this.bubiOrderLineRepository.findAllByVendorIdAndVendorAccount(vendorId, vendorAccount);
    }


    public List<CoreData> getAllCoreData() {
        return this.coreDataRepository.findAll();
    }

    public CoreData getCoreData(String collection, String shelfmark) {
        return this.coreDataRepository.findAllByCollectionAndShelfmark(collection, shelfmark);
    }

    public CoreData saveCoreData(CoreData coreData) {
        return this.coreDataRepository.save(coreData);
    }

    public BubiOrderLine expandBubiOrderLine(BubiOrderLine bubiOrderLine) {
        if (bubiOrderLine.getShelfmark() != null || bubiOrderLine.getCollection() != null) {
            long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(bubiOrderLine.getShelfmark(), bubiOrderLine.getCollection());
            bubiOrderLine.setCounter(counter);
            log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
            addCoreData(bubiOrderLine);
        }
        return bubiOrderLine;
    }

    public BubiOrderLine expandBubiOrderLineFromBarcode(String barcode) {
        Item item = this.itemService.findItemByBarcode(barcode);
        String collection = item.getItemData().getLocation().getValue();
        String shelfmark = item.getHoldingData().getCallNumber();
        long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
        BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
        log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
        bubiOrderLine  = expandBubiOrderLineFromShelfmark(collection, shelfmark);
        if (bubiOrderLine.getTitle() == null || bubiOrderLine.getTitle().isEmpty()) {
            bubiOrderLine.setAlmaHoldingId(item.getHoldingData().getHoldingId());
            bubiOrderLine.setAlmaMmsId(item.getBibData().getMmsId());
            bubiOrderLine.setTitle(item.getBibData().getTitle());
        }
        return bubiOrderLine;
    }

    public BubiOrderLine expandBubiOrderLineFromShelfmark(String collection, String shelfmark) {
        if (shelfmark != null && collection != null) {
            collection = collection.toUpperCase();
            shelfmark = shelfmark.toUpperCase();
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
        return null;
    }

    public BubiOrderLine saveBubiOrderLine(BubiOrderLine bubiOrderLine) {
        bubiOrderLine.setLastChange(new Date());
        return this.bubiOrderLineRepository.save(bubiOrderLine);
    }

    public List<BubiOrderLine> getActiveOrderlines() {
        List<BubiOrderLine> allOpenOrderlines = new ArrayList<>();
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.NEW));
        allOpenOrderlines.addAll(this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING));
        return allOpenOrderlines;
    }

    public List<BubiOrderLine> getWatingOrderlines() {
        return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.WAITING);
    }

    public BubiOrderLine getBubiOrderLineFromIdentifier(String identifier) {
        return this.bubiOrderLineRepository.getBubiOrderLineByBubiOrderLineId(identifier);
    }

    public List<BubiOrderLine> getSentOrderlines() {
        return this.bubiOrderLineRepository.findAllByStatus(BubiStatus.SENT);
    }

    public List<BubiOrderLine> getAllOrderlines() {
        return this.bubiOrderLineRepository.findAll();
    }

    public CoreDataImportRun readCoreDataFromExcelSheet(CoreDataImportRun coreDataImportRun, XSSFWorkbook workbook) {
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            XSSFRow row = worksheet.getRow(i);
            CoreData coreData = new CoreData();
            if (row.getCell(1) == null || row.getCell(0) == null)
                continue;
            String collection = row.getCell(0).getStringCellValue();
            String shelfmark = row.getCell(1).getStringCellValue();
            if (!shelfmark.contains(" Z "))
                shelfmark = shelfmark.replace("Z", " Z ");
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
                coreData.setComment(row.getCell(39).getStringCellValue());
            } catch (Exception e) {
                coreData.setComment("");
            }
            try {
                coreData.setIsFf("j".equals(row.getCell(43).getStringCellValue()));
            } catch (Exception e) {
                coreData.setIsFf(false);
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
            this.coreDataRepository.save(coreData);
        }
        return coreDataImportRun;
    }

    public List<BubiData> listAllBubiData() {
        return this.bubiDataRepository.findAll();
    }

    public BubiData getbubiData(String vendorId, String vendorAccount) {
        return this.bubiDataRepository.getOne(new BubiDataId(vendorId, vendorAccount));
    }

    public BubiData getVendorAccount(String vendorID, String collection) {
        String campus = collection.startsWith("E") ? "E0001" : "D0001";
        List<BubiData> bubiData = this.bubiDataRepository.findByVendorIdAndCampus(vendorID, campus);
        if (bubiData.size() == 0)
            return new BubiData();
        else
            return bubiData.get(0);
    }

    public void deleteBubiData(String vendorId, String vendorAccount) {
        this.bubiDataRepository.deleteById(new BubiDataId(vendorId, vendorAccount));
    }

    public BubiData saveBubiData(BubiData bubiData) {
        return this.bubiDataRepository.save(bubiData);
    }

    public BubiOrderLine getBubiOrderLineByAlmaPoLineId(String almaPoLineId) {
        return this.bubiOrderLineRepository.getBubiOrderLineByAlmaPoLineId(almaPoLineId);
    }

    private boolean addCoreData(BubiOrderLine bubiOrderLine) {
        CoreData coredata = this.coreDataRepository.findAllByCollectionAndShelfmark(bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark());
        if (coredata != null) {
            bubiOrderLine.addCoreData(coredata);
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
        } else if (bubiOrderline.isFf()) {
            bubiOrderline.setFund(journalFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        } else {
            bubiOrderline.setFund(monographFund);
            bubiOrderline.setPrice(bubiData.getStandardPriceMonograph());
        }
    }

    public List<BubiOrder> packBubiOrder(BubiOrder bubiOrder) {
        Hashtable<String, BubiOrder> bubiOrders = new Hashtable<>();
        for (int i = 0; i< bubiOrder.getBubiOrderLines().size(); i++) {
            BubiOrderLine bubiOrderLine = bubiOrder.getBubiOrderLines().get(i);

            PoLine poLine = buildPoLine(bubiOrderLine);
            poLine = almaPoLineService.savePoLine(poLine);
            bubiOrderLine.setAlmaPoLineId(poLine.getNumber());
            bubiOrderLine.setPositionalNumber(i +1);
            bubiOrderLine.setStatus(BubiStatus.WAITING);
            bubiOrderLine.setLastChange(new Date());
            String key = bubiOrderLine.getVendorId() + "-" + bubiOrderLine.getVendorAccount();
            if (bubiOrders.containsKey(key)) {
                bubiOrders.get(key).addBubiOrderLine(bubiOrderLine);
                this.bubiOrderRepository.save(bubiOrder);
            } else {
                long counter = this.bubiOrderRepository.countAllByVendorIdAndVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getVendorAccount()) + 1;
                String bubiOrderId = key +"-" + counter;
                BubiOrder bubiOrderInd = new BubiOrder(bubiOrderLine);
                bubiOrderInd.setBubiOrderId(bubiOrderId);
                this.bubiOrderRepository.save(bubiOrderInd);
                bubiOrders.put(key, bubiOrderInd);
            }
            this.bubiOrderLineRepository.save(bubiOrderLine);
        }

        // TODO: start packaging job
        return new ArrayList<>(bubiOrders.values());
    }

    public BubiOrder payBubiOrder(BubiOrder bubiOrder) {
        Invoice invoice = getInvoiceForBubiOrder(bubiOrder);
        this.almaInvoiceServices.saveInvoice(invoice);
        return bubiOrder;
    }
}

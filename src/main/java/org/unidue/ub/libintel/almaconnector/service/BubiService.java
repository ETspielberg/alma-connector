package org.unidue.ub.libintel.almaconnector.service;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderLineRepository;
import org.unidue.ub.libintel.almaconnector.repository.BubiOrderRepository;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;

import java.util.List;

@Service
public class BubiService {


    private final BubiOrderRepository bubiOrderRepository;

    private final BubiOrderLineRepository bubiOrderLineRepository;

    private final CoreDataRepository coreDataRepository;

    private final PrimoService primoService;

    private final Logger log = LoggerFactory.getLogger(BubiService.class);

    public BubiService(
            BubiOrderRepository bubiOrderRepository,
            BubiOrderLineRepository bubiOrderLineRepository,
            CoreDataRepository coreDataRepository,
            PrimoService primoService) {
        this.bubiOrderLineRepository = bubiOrderLineRepository;
        this.bubiOrderRepository = bubiOrderRepository;
        this.coreDataRepository = coreDataRepository;
        this.primoService = primoService;
    }

    public BubiOrder getBubiOrders(String orderNumber) {
        return this.bubiOrderRepository.getOne(orderNumber);
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
            CoreData coredata = this.coreDataRepository.findAllByCollectionAndShelfmark(bubiOrderLine.getCollection(),
                    bubiOrderLine.getShelfmark());
            if (coredata != null)
                bubiOrderLine.addCoreData(coredata);
        }
        return bubiOrderLine;
    }

    public BubiOrderLine expandBubiOrderLine(String collection, String shelfmark) {
        if (shelfmark != null || collection != null) {
            long counter = this.bubiOrderLineRepository.countAllByShelfmarkAndCollection(shelfmark, collection);
            BubiOrderLine bubiOrderLine = new BubiOrderLine(collection, shelfmark, counter);
            log.info(String.format("retrieving core data for collection %s and shelfmark %s", bubiOrderLine.getCollection(), bubiOrderLine.getShelfmark()));
            CoreData coredata = this.coreDataRepository.findAllByCollectionAndShelfmark(collection,shelfmark);
            if (coredata != null)
                bubiOrderLine.addCoreData(coredata);
            return bubiOrderLine;
        }
        return null;
    }

    public BubiOrderLine saveBubiOrderLine(BubiOrderLine bubiOrderLine) {
        return this.bubiOrderLineRepository.save(bubiOrderLine);
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
                coreData.setBubiData(row.getCell(11).getStringCellValue());
            } catch (Exception e) {
                coreData.setBubiData("");
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
            AlmaJournalData almaJournalData = new AlmaJournalData(coreData.getCollection(), coreData.getShelfmark());
            List<AlmaJournalData> foundData = this.primoService.getPrimoResponse(almaJournalData);
            if (foundData.size() == 1) {
                coreData.setAlmaMmsId(foundData.get(0).mmsId);
                coreData.setAlmaHoldingId(foundData.get(0).holdingId);
            } else if (foundData.size() > 1) {
                for (AlmaJournalData foundDatum: foundData) {
                    if (coreData.getTitle().equals(foundDatum.title)) {
                        coreData.setAlmaMmsId(foundData.get(0).mmsId);
                        coreData.setAlmaHoldingId(foundData.get(0).holdingId);
                    }

                }
            }

            this.coreDataRepository.save(coreData);
        }
        return coreDataImportRun;
    }
}

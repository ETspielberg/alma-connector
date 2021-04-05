package org.unidue.ub.libintel.almaconnector.service.bubi;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.AlmaItemData;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreDataImportRun;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class CoreDataService {

    private final CoreDataRepository coreDataRepository;

    private final PrimoService primoService;

    CoreDataService(CoreDataRepository coreDataRepository,
                    PrimoService primoService) {
        this.coreDataRepository = coreDataRepository;
        this.primoService = primoService;
    }

    public List<CoreData> getActiveCoreData() {
        return this.coreDataRepository.findAllByActiveOrderByMinting(true);
    }

    public List<CoreData> getAllCoreData() {
        return this.coreDataRepository.findAll();
    }

    public CoreData getForCollectionAndShelfmark(String collection, String shelfmark) {
        return this.coreDataRepository.findAllByCollectionAndShelfmark(collection, shelfmark);
    }

    public CoreData findDefaultForMaterial(String material, String campus) {
        return this.coreDataRepository.findCoreDataByActiveAndShelfmarkAndMediaTypeOrderByMinting(true, "STANDARD_" + campus, material);
    }

    public CoreData saveCoreData(CoreData coreData) {
        return this.coreDataRepository.save(coreData);
    }

    public void deleteCoreData(String coreDataId) {
        this.coreDataRepository.deleteById(coreDataId);
    }

    public CoreDataImportRun readCoreDataFromExcelSheet(CoreDataImportRun coreDataImportRun, XSSFWorkbook workbook) {
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            CoreData coreData = new CoreData();
            if (row.getCell(1) == null)
                continue;
            String collection;
            try {
                collection = row.getCell(0).getStringCellValue();
            } catch (Exception e) {
                collection = "";
            }
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
                coreData.setVendorAccount(row.getCell(11).getStringCellValue());
            } catch (Exception e) {
                coreData.setVendorAccount("");
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
            coreDataImportRun.addCoreData(coreData);
            if (coreData.isActive()) {
                AlmaItemData almaItemData = new AlmaItemData(coreData.getCollection(), coreData.getShelfmark());
                almaItemData.mediaType = "book";
                if (almaItemData.shelfmark.contains(" Z "))
                    almaItemData.mediaType = "journal";
                List<AlmaItemData> foundData = this.primoService.getPrimoResponse(almaItemData);
                if (foundData.size() == 1) {
                    coreData.setAlmaMmsId(foundData.get(0).mmsId);
                    coreData.setAlmaHoldingId(foundData.get(0).holdingId);
                    coreData.setTitle(foundData.get(0).title);
                    coreData.setCollection(foundData.get(0).collection);
                    this.coreDataRepository.save(coreData);
                } else if (foundData.size() > 1) {
                    for (AlmaItemData foundDatum : foundData) {
                        CoreData coreDataInd = coreData.clone();
                        coreDataInd.setAlmaMmsId(foundDatum.mmsId);
                        coreDataInd.setAlmaHoldingId(foundDatum.holdingId);
                        coreDataInd.setTitle(foundDatum.title);
                        coreDataInd.setCollection(foundDatum.collection);
                        this.coreDataRepository.save(coreDataInd);
                    }
                } else {
                    coreData.setActive(false);
                    this.coreDataRepository.save(coreData);
                }
            }
        }
        return coreDataImportRun;
    }
}

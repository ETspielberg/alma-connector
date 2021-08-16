package org.unidue.ub.libintel.almaconnector.service.bubi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.bubi.MediaType;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.AlmaItemData;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.CoreDataBriefDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.CoreDataFullDto;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.CoreData;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.CoreDataImportRun;
import org.unidue.ub.libintel.almaconnector.repository.CoreDataRepository;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;

import java.util.ArrayList;
import java.util.List;

/**
 * offers functions around core data for bubi order lines
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class CoreDataService {

    private final CoreDataRepository coreDataRepository;

    private final PrimoService primoService;

    /**
     * constructor based autowiring of the core data repository and the primo service to extend the journal data
     *
     * @param coreDataRepository the core data repository
     * @param primoService       the primo service
     */
    CoreDataService(CoreDataRepository coreDataRepository,
                    PrimoService primoService) {
        this.coreDataRepository = coreDataRepository;
        this.primoService = primoService;
    }

    /**
     * retrieves all core data marked as active
     *
     * @return a list of <class>CoreData</class> objects
     */
    public List<CoreDataBriefDto> getCoreData(String mode) {
        List<CoreDataBriefDto> coreData = new ArrayList<>();
        if ("active".equals(mode))
            this.coreDataRepository.findAllByActiveOrderByMinting(true).forEach(entry -> coreData.add(new CoreDataBriefDto(entry)));
        else
            this.coreDataRepository.findAll().forEach(entry -> coreData.add(new CoreDataBriefDto(entry)));
        return coreData;
    }

    public CoreDataFullDto getCoreDatum(String coreDataId) {
        CoreData coreData = this.coreDataRepository.findById(coreDataId).orElse(null);
        if (coreData == null)
            return null;
        else
            return new CoreDataFullDto(coreData);
    }

    /**
     * retrieves core data for an item with by its collection and shelfmark
     *
     * @param collection the collection of the item
     * @param shelfmark  the shelfmark of the item
     * @return the core data for this item
     */
    public CoreData getForCollectionAndShelfmark(String collection, String shelfmark) {
        return this.coreDataRepository.findAllByCollectionAndShelfmark(collection, shelfmark);
    }

    /**
     * retreives the default core data for a given material type
     *
     * @param material the material type
     * @param campus   the campus for which the mdefault is obtained
     * @return the default core data for this type of items
     */
    public CoreData findDefaultForMaterial(String material, String campus) {
        return this.coreDataRepository.findCoreDataByActiveAndShelfmarkAndMediaTypeOrderByMinting(true, "STANDARD_" + campus, material);
    }

    /**
     * saves the core data for an item
     *
     * @param coreDataFullDto the core data to be saved
     * @return the saved core data
     */
    public CoreDataFullDto saveCoreData(CoreDataFullDto coreDataFullDto) {
        CoreData coreData = this.coreDataRepository.findById(coreDataFullDto.getCoreDataId()).orElse(new CoreData());
        coreData = this.coreDataRepository.save(coreDataFullDto.updateCoreData(coreData));
        return new CoreDataFullDto(coreData);
    }

    /**
     * deletes the core data for an item
     *
     * @param coreDataId hte id of the core data to be deleted
     */
    public void deleteCoreData(String coreDataId) {
        this.coreDataRepository.deleteById(coreDataId);
    }

    /**
     * loads a list of core data from an excel file
     *
     * @param coreDataImportRun the import session object
     * @param workbook          the excel workbook to be imported
     * @return the import session object
     */
    public CoreDataImportRun readCoreDataFromExcelSheet(CoreDataImportRun coreDataImportRun, XSSFWorkbook workbook) {
        // retrieve first sheet
        XSSFSheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            CoreData coreData = new CoreData();
            if (row.getCell(1) == null)
                continue;

            coreData.setCollection(row.getCell(0).getStringCellValue());
            coreData.setMediaType(row.getCell(1).getStringCellValue());
            coreData.calculateId();
            coreData.setShelfmark(row.getCell(2).getStringCellValue());
            coreData.setMinting(getValue(3, row));
            coreData.setColor(getValue(4, row));
            coreData.setColorMinting(getValue(5, row));
            coreData.setBinding(getValue(6, row));
            coreData.setCover(getValue(7, row));
            coreData.setPositionYear(getValue(8, row));
            coreData.setPositionVolume(getValue(9, row));
            coreData.setPositionPart(getValue(10, row));
            coreData.setPositionDescription(getValue(11, row));
            coreData.setComment(getValue(12, row));
            coreData.setBindingsFollow(getValue(13, row));
            coreData.setVendorAccount(getValue(14, row));
            coreData.setInternalNote(getValue(15, row));
            coreData.setBubiNote(getValue(16, row));
            coreData.setActive(row.getCell(17).getBooleanCellValue());
            coreData.setFund(getValue(18, row));
            coreDataImportRun.addCoreData(coreData);
            if (coreData.isActive()) {
                AlmaItemData almaItemData = new AlmaItemData(coreData.getCollection(), coreData.getShelfmark());
                almaItemData.mediaType = MediaType.BOOK.name();
                if (almaItemData.shelfmark.contains(" Z "))
                    almaItemData.mediaType = MediaType.JOURNAL.name();
                List<AlmaItemData> foundData = this.primoService.getPrimoResponse(almaItemData);
                if (foundData.size() == 1) {
                    coreData.setAlmaMmsId(foundData.get(0).mmsId);
                    coreData.setAlmaHoldingId(foundData.get(0).holdingId);
                    coreData.setTitle(foundData.get(0).title);
                    this.coreDataRepository.save(coreData);
                } else if (foundData.size() > 1) {
                    for (AlmaItemData foundDatum : foundData) {
                        CoreData coreDataInd = coreData.clone();
                        coreDataInd.setAlmaMmsId(foundDatum.mmsId);
                        coreDataInd.setAlmaHoldingId(foundDatum.holdingId);
                        coreDataInd.setTitle(foundDatum.title);
                        coreDataInd.setCollection(foundDatum.collection);
                        coreData.calculateId();
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

    private String getValue(int column, XSSFRow row) {
        try {
            return row.getCell(column).getStringCellValue();
        } catch (Exception e) {
            return "";
        }
    }

    public CoreDataFullDto createCoredataFromShelfmark(String collection, String shelfmark) {
        CoreDataFullDto coreDataFullDto = new CoreDataFullDto();
        AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
        List<AlmaItemData> foundData = this.primoService.getPrimoResponse(almaItemData);
        if (foundData.size() == 1) {
            almaItemData = foundData.get(0);
            coreDataFullDto.setAlmaHoldingId(almaItemData.holdingId);
            coreDataFullDto.setAlmaMmsId(almaItemData.mmsId);
            coreDataFullDto.setCollection(almaItemData.collection);
            coreDataFullDto.setShelfmark(almaItemData.shelfmark);
            coreDataFullDto.setMediaType(almaItemData.mediaType);
            coreDataFullDto.setTitle(almaItemData.title);
            coreDataFullDto.setMinting(almaItemData.title);
            coreDataFullDto.setStandard(!"JOURNAL".equals(almaItemData.mediaType));
        }
        return coreDataFullDto;
    }
}

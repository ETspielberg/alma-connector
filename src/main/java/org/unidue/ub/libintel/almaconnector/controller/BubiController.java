package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.service.AlmaPoLineService;
import org.unidue.ub.libintel.almaconnector.service.BubiService;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;

import java.io.IOException;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.buildPoLine;

@Controller
@RequestMapping("/bubi")
public class BubiController {

    private final BubiService bubiService;

    private final PrimoService primoService;

    private final AlmaPoLineService almaPoLineService;

    private final Logger log = LoggerFactory.getLogger(BubiController.class);

    BubiController(BubiService bubiService,
                   AlmaPoLineService almaPoLineService,
                   PrimoService primoService) {
        this.bubiService = bubiService;
        this.almaPoLineService = almaPoLineService;
        this.primoService = primoService;
    }

    @GetMapping("/start")
    public String getStartPage() {
        return "bubi/start";
    }

    // ---------------------- Bubi data endpoints ----------------------

    @GetMapping("/bubiData")
    public String getAllBubiData(Model model) {
        model.addAttribute("bubiDataList", this.bubiService.listAllBubiData());
        return "bubi/data/overview";
    }

    @GetMapping("/bubiData/edit")
    public String getAllBubiData(Model model, String vendorId, String vendorAccount) {
        model.addAttribute("bubiData", this.bubiService.getbubiData(vendorId, vendorAccount));
        return "bubi/data/edit";
    }

    @PostMapping("/bubiData")
    public String saveBubiData(@ModelAttribute("bubiData") BubiData bubiData, Model model) {
        bubiData = this.bubiService.saveBubiData(bubiData);
        model.addAttribute("bubiData", bubiData);
        return "bubi/data/editSuccess";
    }

    @GetMapping("/newBubiData")
    public String createNewBubiData(Model model) {
        model.addAttribute("bubiData", new BubiData());
        return "bubi/data/edit";
    }

    // ---------------------- Core data endpoints ----------------------

    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     * @param model the model object
     * @return returns a status of 200 if the import was successful
     */
    @GetMapping("/coredata")
    public String showCoreData(Model model) {
        model.addAttribute("coredataList", this.bubiService.getAllCoreData());
        return "bubi/coredata/overview";
    }

    @GetMapping("/coredata/edit")
    public String editCoreData(Model model, String collection, String shelfmark) {
        model.addAttribute("coredata", this.bubiService.getCoreData(collection, shelfmark));
        model.addAttribute("bubiList", this.bubiService.listAllBubiData());
        return "bubi/coredata/edit";
    }

    @PostMapping("/coredata")
    public String saveCoreData(@ModelAttribute("coredata") CoreData coredata, Model model) {
        model.addAttribute("coredata", this.bubiService.saveCoreData(coredata));
        return "bubi/coredata/editSuccess";
    }

    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     * @param bubiCoreDataFile the result xlsx file holding the bubi core data
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/coredata/import")
    public ResponseEntity<CoreDataImportRun> updateInvoicesWithSapData(@RequestParam("file") MultipartFile bubiCoreDataFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(bubiCoreDataFile.getInputStream());
        CoreDataImportRun coreDataImportRun = new CoreDataImportRun();
        coreDataImportRun = this.bubiService.readCoreDataFromExcelSheet(coreDataImportRun, workbook);
        return ResponseEntity.ok(coreDataImportRun);
    }

    // ---------------------- bubi order line endpoints ----------------------

    @GetMapping("/orderline/new")
    public String getNewBubiOrderLinePage(Model model) {
        return "bubi/orderline/new";
    }

    @GetMapping("/orderline")
    public String getAllOrderlines(Model model) {
        model.addAttribute("orderlines", this.bubiService.getAllBubiOrderLines());
        return "bubi/orderline/overview";
    }

    @PostMapping("/orderLine")
    public String createNewBubiOrderLine(@ModelAttribute("orderline") BubiOrderLine bubiOrderLine, Model model) {
        model.addAttribute("orderline", this.bubiService.expandBubiOrderLine(bubiOrderLine));
        return "bubi/orderline/edit";
    }

    @GetMapping("/orderline/fromShelfmark")
    public String createBubiOrderLineFromShelfmark(Model model, String collection, String shelfmark) {
        model.addAttribute("orderline", this.bubiService.expandBubiOrderLine(collection, shelfmark));
        log.info("rendering new bubi order");
        return "bubi/orderline/edit";
    }

    @GetMapping("/orderline/fromBarcode")
    public String createBubiOrderLinePageFromBarcode(Model model, String barcode) {
        model.addAttribute("orderline", new BubiOrderLine());
        return "bubi/orderline/edit";
    }

    @PostMapping("/orderline/save")
    public String saveBubiOrderLine(@ModelAttribute("orderline") BubiOrderLine bubiOrderLine, Model model) {
        bubiOrderLine = this.bubiService.saveBubiOrderLine(bubiOrderLine);
        BubiData bubiData = this.bubiService.getBubiDataForString(bubiOrderLine.getVendorId());
        PoLine poLine = buildPoLine(bubiOrderLine, bubiData);
        log.info(poLine.toString());
        poLine = almaPoLineService.savePoLine(poLine);
        bubiOrderLine.setAlmaPoLineId(poLine.getNumber());
        bubiOrderLine = bubiService.saveBubiOrderLine(bubiOrderLine);
        model.addAttribute("bubiOrderLine", bubiOrderLine);
        return "bubi/orderline/orderline";
    }

    // ---------------------- primo data endpoints ----------------------

    @GetMapping("/getJournalData")
    public ResponseEntity<List<AlmaJournalData>> getJournalData(String collection, String shelfmark) {
        AlmaJournalData almaJournalData = new AlmaJournalData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaJournalData));
    }






}

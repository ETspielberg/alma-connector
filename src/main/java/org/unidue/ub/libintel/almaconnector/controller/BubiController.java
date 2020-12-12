package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaPoLinesApiClient;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaVendorApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.AlmaJournalData;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreData;
import org.unidue.ub.libintel.almaconnector.model.bubi.CoreDataImportRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.service.AlmaPoLineService;
import org.unidue.ub.libintel.almaconnector.service.BubiService;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;
import org.unidue.ub.libintel.almaconnector.service.VendorService;

import java.io.IOException;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.Utils.buildPoLine;
import static org.unidue.ub.libintel.almaconnector.Utils.getFromExcel;

@Controller
@RequestMapping("/bubi")
public class BubiController {

    private final BubiService bubiService;

    private final PrimoService primoService;

    private final VendorService vendorService;

    private final AlmaPoLineService almaPoLineService;

    private final Logger log = LoggerFactory.getLogger(BubiController.class);

    BubiController(BubiService bubiService,
                   AlmaPoLineService almaPoLineService,
                   VendorService vendorService,
                   PrimoService primoService) {
        this.bubiService = bubiService;
        this.almaPoLineService = almaPoLineService;
        this.vendorService = vendorService;
        this.primoService = primoService;
    }

    @GetMapping("/start")
    public String getStartPage() {
        return "bubiStart";
    }


    @GetMapping("/coredata/list")
    public String getCoredataListPage(Model model) {
        model.addAttribute("coreData", this.bubiService.getAllCoreData());
        return "coredataList";
    }

    @GetMapping("/bubiOrderLineNew")
    public String getNewBubiOrderLinePage(Model model) {
        model.addAttribute("bubiOrderLine", new BubiOrderLine());
        return "bubiOrderLineNew";
    }

    @PostMapping("/newBubiOrderLine")
    public String createNewBubiOrderLine(@ModelAttribute("bubiOrderLine") BubiOrderLine bubiOrderLine, Model model) {
        model.addAttribute("bubiOrderLine", this.bubiService.expandBubiOrderLine(bubiOrderLine));
        return "bubiOrderLineEdit";
    }

    @GetMapping("/newBubiOrderLine")
    public String createNewBubiOrderLine(Model model, String collection, String shelfmark) {
        model.addAttribute("bubiOrderLine", this.bubiService.expandBubiOrderLine(collection, shelfmark));
        log.info("rendering new bubi order");
        return "bubiOrderLineOverview";
    }

    @PostMapping("/saveBubiOrderLine")
    public String saveBubiOrderLine(@ModelAttribute("bubiOrderLine") BubiOrderLine bubiOrderLine, Model model) {
        bubiOrderLine = this.bubiService.saveBubiOrderLine(bubiOrderLine);
        Vendor vendor = this.vendorService.getVendorAccount(bubiOrderLine.getVendorId());
        PoLine poLine = buildPoLine(bubiOrderLine, vendor);
        poLine = almaPoLineService.savePoLine(poLine);
        bubiOrderLine.setAlmaPoLineId(poLine.getPoNumber());
        model.addAttribute("bubiOrderLine", bubiOrderLine);
        return "bubiOrderLineEditSuccess";
    }

    @GetMapping("/getJournalData")
    public ResponseEntity<List<AlmaJournalData>> getJournalData(String collection, String shelfmark) {
        AlmaJournalData almaJournalData = new AlmaJournalData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaJournalData));
    }


    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     * @param bubiCoreDataFile the result xlsx file holding the bubi core data
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/coredataImport")
    public ResponseEntity<CoreDataImportRun> updateInvoicesWithSapData(@RequestParam("file") MultipartFile bubiCoreDataFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(bubiCoreDataFile.getInputStream());
        CoreDataImportRun coreDataImportRun = new CoreDataImportRun();
        coreDataImportRun = this.bubiService.readCoreDataFromExcelSheet(coreDataImportRun, workbook);
        return ResponseEntity.ok(coreDataImportRun);
    }

    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     * @param model the model object
     * @return returns a status of 200 if the import was successful
     */
    @GetMapping("/coredataOverview")
    public String showCoreData(Model model) {
        model.addAttribute("coredataList", this.bubiService.getAllCoreData());
        return "bubiCoredataOverview";
    }

    @GetMapping("/coredataEdit")
    public String editCoreData(Model model, String collection, String shelfmark) {
        model.addAttribute("coredata", this.bubiService.getCoreData(collection, shelfmark));
        return "bubiCoredataEdit";
    }

    @PostMapping("/coredataEdit")
    public String saveCoreData(@ModelAttribute("coredata") CoreData coredata, Model model) {
        model.addAttribute("coredata", this.bubiService.saveCoreData(coredata));
        return "bubiCoredataEditSuccess";
    }
}

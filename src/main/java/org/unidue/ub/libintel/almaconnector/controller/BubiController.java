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
import java.util.ArrayList;
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

    @GetMapping("/bubiData/new")
    public String createNewBubiData(Model model) {
        model.addAttribute("bubiData", new BubiData());
        return "bubi/data/edit";
    }

    @PostMapping("/bubiData/delete")
    public String deletebubiData(String vendorId, String vendorAccount) {
        this.bubiService.deleteBubiData(vendorId, vendorAccount);
        return "bubi/data/deleteSuccess";
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
    public String getAllOrderlines(Model model,
                                   @RequestParam(value = "vendorId", required = false) String vendorId,
                                   @RequestParam(value = "vendorAccount", required = false) String vendorAccount,
                                   @RequestParam(value = "status", required = false) String status
                                   ) {
        List<BubiOrderLine> orderlines;
        if (vendorId != null) {
            if (vendorAccount != null)
                orderlines = this.bubiService.getAllBubiOrderLinesForVendorAccoutn(vendorId, vendorAccount);
            else
                orderlines = this.bubiService.getAllBubiOrderLinesForBubi(vendorId);
        }
        else
            orderlines = this.bubiService.getAllBubiOrderLines();
        model.addAttribute("orderlines",orderlines);
        return "bubi/orderline/overview";
    }

    @PostMapping("/orderLine")
    public String createNewBubiOrderLine(@ModelAttribute("orderline") BubiOrderLine bubiOrderLine, Model model) {
        model.addAttribute("orderline", this.bubiService.expandBubiOrderLine(bubiOrderLine));
        return "bubi/orderline/edit";
    }

    @GetMapping("/orderline/fromShelfmark")
    public String createBubiOrderLineFromShelfmark(Model model, String collection, String shelfmark) {
        model.addAttribute("orderline", this.bubiService.expandBubiOrderLine(collection.strip(), shelfmark.strip()));
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
        String vendoraccount = this.bubiService.getVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getCollection()).getVendorAccount();
        bubiOrderLine.setVendorAccount(vendoraccount);
        bubiOrderLine = this.bubiService.saveBubiOrderLine(bubiOrderLine);
        PoLine poLine = buildPoLine(bubiOrderLine);
        log.info(poLine.toString());
        poLine = almaPoLineService.savePoLine(poLine);
        bubiOrderLine.setAlmaPoLineId(poLine.getNumber());
        bubiOrderLine = bubiService.saveBubiOrderLine(bubiOrderLine);
        model.addAttribute("bubiOrderLine", bubiOrderLine);
        return "bubi/orderline/editSuccess";
    }

    @PostMapping("/orderline/pack")
    public String packOrders( @RequestParam(value = "cers" , required = false) String[] almaPoLineIds, Model model) {
        List<BubiOrderLine> allOrderLines = new ArrayList<>();
        if(almaPoLineIds != null) {
            for (String almaPoLineId : almaPoLineIds)
                allOrderLines.add(bubiService.getBubiOrderLineByAlmaPoLineId(almaPoLineId));
        }
        BubiOrder bubiOrder = new BubiOrder();
        bubiOrder.setBubiOrderLines(allOrderLines);
        bubiOrder.setComment("new");
        model.addAttribute("bubiOrder", bubiOrder);
        return "bubi/orderline/packSuccess";
    }

    // ---------------------- primo data endpoints ----------------------

    @GetMapping("/getJournalData")
    public ResponseEntity<List<AlmaJournalData>> getJournalData(String collection, String shelfmark) {
        AlmaJournalData almaJournalData = new AlmaJournalData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaJournalData));
    }






}

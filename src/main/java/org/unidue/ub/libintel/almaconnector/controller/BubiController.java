package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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

@RestController
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

    // ---------------------- Bubi data endpoints ----------------------

    @GetMapping("/bubidata/all")
    private ResponseEntity<List<BubiData>> getAllbubiData() {
        return ResponseEntity.ok(this.bubiService.listAllBubiData());
    }

    // ---------------------- Core data endpoints ----------------------

    @GetMapping("/coredata/all")
    private ResponseEntity<List<CoreData>> getAllCoreData() {
        return ResponseEntity.ok(this.bubiService.getAllCoreData());
    }

    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     * @param bubiCoreDataFile the result xlsx file holding the bubi core data
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/coredata/import")
    public ResponseEntity<CoreDataImportRun> importCoredata(@RequestParam("file") MultipartFile bubiCoreDataFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(bubiCoreDataFile.getInputStream());
        CoreDataImportRun coreDataImportRun = new CoreDataImportRun();
        coreDataImportRun = this.bubiService.readCoreDataFromExcelSheet(coreDataImportRun, workbook);
        return ResponseEntity.ok(coreDataImportRun);
    }

    @PostMapping("/coredata/save")
    public ResponseEntity<CoreData> saveCoredata(@RequestBody CoreData coredata) {
        log.info(coredata.getMediaType());
        return ResponseEntity.ok(this.bubiService.saveCoreData(coredata));
    }


    // ---------------------- bubi order line endpoints ----------------------

    @PostMapping("/orderline/save")
    public ResponseEntity<BubiOrderLine> saveBubiOrderLine(@RequestBody BubiOrderLine bubiOrderLine) {
        String vendoraccount = this.bubiService.getVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getCollection()).getVendorAccount();
        bubiOrderLine.setVendorAccount(vendoraccount);
        bubiOrderLine = this.bubiService.saveBubiOrderLine(bubiOrderLine);
        return ResponseEntity.ok(bubiOrderLine);
    }

    @GetMapping("/orderline/fromShelfmark")
    public ResponseEntity<BubiOrderLine> getForShelfmark(String shelfmark, String collection) {
        return ResponseEntity.ok(this.bubiService.expandBubiOrderLineFromShelfmark(collection, shelfmark));
    }

    @GetMapping("/orderline/fromBarcode")
    public ResponseEntity<BubiOrderLine> getForBarcode(String barcode) {
        return ResponseEntity.ok(this.bubiService.expandBubiOrderLineFromBarcode(barcode));
    }

    @GetMapping("/orderline/fromIdentifier")
    public ResponseEntity<BubiOrderLine> getForIdentifier(String identifier) {
        return ResponseEntity.ok(this.bubiService.getBubiOrderLineFromIdentifier(identifier));
    }

    @GetMapping("/orderline/active")
    public ResponseEntity<List<BubiOrderLine>> getAllActiveOrderlines() {
        return ResponseEntity.ok(this.bubiService.getActiveOrderlines());
    }

    @GetMapping("/orderline/waiting")
    public ResponseEntity<List<BubiOrderLine>> getAllWaitingOrderlines() {
        return ResponseEntity.ok(this.bubiService.getWatingOrderlines());
    }

    @GetMapping("/orderline/sent")
    public ResponseEntity<List<BubiOrderLine>> getAllSentOrderlines() {
        return ResponseEntity.ok(this.bubiService.getSentOrderlines());
    }

    @GetMapping("/orderline/all")
    public ResponseEntity<List<BubiOrderLine>> getAllOrderlines() {
        return ResponseEntity.ok(this.bubiService.getAllOrderlines());
    }

    @GetMapping("/orderline/bubi/{vendorId}")
    public ResponseEntity<List<BubiOrderLine>> getAllOrderlines(@PathVariable String vendorId) {
        return ResponseEntity.ok(this.bubiService.getAllBubiOrderLinesForBubi(vendorId));
    }

    // ---------------------- bubi order endpoints ----------------------

    @GetMapping("/order/all")
    public ResponseEntity<List<BubiOrder>> getAllOrders() {
        return ResponseEntity.ok(this.bubiService.getAllBubiOrder());
    }

    @GetMapping("/order/active")
    public ResponseEntity<List<BubiOrder>> getActiveOrders() {
        return ResponseEntity.ok(this.bubiService.getActiveBubiOrder());
    }


    @PostMapping("/order/save")
    public ResponseEntity<List<BubiOrder>> packOrders( @RequestBody BubiOrder bubiOrder) {
        return ResponseEntity.ok(this.bubiService.packBubiOrder(bubiOrder));
    }

    @PostMapping("/order/pay")
    public ResponseEntity<BubiOrder> payOrder( @RequestBody BubiOrder bubiOrder) {
        return ResponseEntity.ok(this.bubiService.payBubiOrder(bubiOrder));
    }

    // ---------------------- primo data endpoints ----------------------

    @GetMapping("/getJournalData")
    public ResponseEntity<List<AlmaItemData>> getJournalData(String collection, String shelfmark) {
        AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaItemData));
    }
}

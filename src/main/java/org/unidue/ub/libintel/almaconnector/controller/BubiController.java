package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.libintel.almaconnector.model.bubi.*;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaPoLineService;
import org.unidue.ub.libintel.almaconnector.service.bubi.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/bubi")
public class BubiController {

    private final CoreDataService coreDataService;

    private final BubiOrderService bubiOrderService;

    private final BubiDataService bubiDataService;

    private final BubiOrderLineService bubiOrderLineService;

    private final BubiPricesService bubiPricesService;

    private final AlmaPoLineService almaPoLineService;

    private final PrimoService primoService;

    private final Logger log = LoggerFactory.getLogger(BubiController.class);

    BubiController(PrimoService primoService,
                   BubiOrderService bubiOrderService,
                   BubiDataService bubiDataService,
                   CoreDataService coreDataService,
                   AlmaPoLineService almaPoLineService,
                   BubiOrderLineService bubiOrderLineService,
                   BubiPricesService bubiPricesService) {
        this.primoService = primoService;
        this.bubiOrderService = bubiOrderService;
        this.bubiDataService = bubiDataService;
        this.coreDataService = coreDataService;
        this.bubiOrderLineService = bubiOrderLineService;
        this.almaPoLineService = almaPoLineService;
        this.bubiPricesService = bubiPricesService;
    }

    // ---------------------- Bubi data endpoints ----------------------

    @GetMapping("/bubidata/all")
    private ResponseEntity<List<BubiData>> getAllbubiData() {
        return ResponseEntity.ok(this.bubiDataService.listAllBubiData());
    }

    @GetMapping("/bubidata/active")
    private ResponseEntity<List<BubiData>> getActivebubiData() {
        return ResponseEntity.ok(this.bubiDataService.listActiveBubiData());
    }

    @GetMapping("/bubiprices/{vendorAccount}")
    private ResponseEntity<List<BubiPrice>> getBubiPrices(@PathVariable String vendorAccount) {
        List<BubiPrice> bubiPrices = this.bubiPricesService.getBubiPricesForVendorAccount(vendorAccount);
        if (bubiPrices == null || bubiPrices.size() == 0)
            bubiPrices = this.bubiPricesService.createNewBubiPricesForVendorAccount(vendorAccount);
        return ResponseEntity.ok(bubiPrices);
    }


    // ---------------------- Core data endpoints ----------------------

    @GetMapping("/coredata/all")
    private ResponseEntity<List<CoreData>> getAllCoreData() {
        return ResponseEntity.ok(this.coreDataService.getAllCoreData());
    }

    @GetMapping("/coredata/active")
    private ResponseEntity<List<CoreData>> getActiveCoreData() {
        return ResponseEntity.ok(this.coreDataService.getActiveCoreData());
    }

    /**
     * receives the bubi core data  as xlsx file and saves them to the database
     *
     * @param bubiCoreDataFile the result xlsx file holding the bubi core data
     * @return returns a status of 200 if the import was successful
     * @throws IOException thrown if the file could not be read
     */
    @PostMapping("/coredata/import")
    public ResponseEntity<CoreDataImportRun> importCoredata(@RequestParam("file") MultipartFile bubiCoreDataFile) throws IOException {
        // read the excel spreadsheet from the request
        XSSFWorkbook workbook = new XSSFWorkbook(bubiCoreDataFile.getInputStream());
        CoreDataImportRun coreDataImportRun = new CoreDataImportRun();
        coreDataImportRun = this.coreDataService.readCoreDataFromExcelSheet(coreDataImportRun, workbook);
        return ResponseEntity.ok(coreDataImportRun);
    }

    @PostMapping("/coredata/save")
    public ResponseEntity<CoreData> saveCoredata(@RequestBody CoreData coredata) {
        return ResponseEntity.ok(this.coreDataService.saveCoreData(coredata));
    }

    @DeleteMapping("/coredata/delete")
    public ResponseEntity<CoreData> deleteCoreData(String collection, String shelfmark) {
        log.info(String.format("deleting core data for %s: %s", collection, shelfmark));
        this.coreDataService.deleteCoreData(collection + "-" + shelfmark);
        return ResponseEntity.ok().build();
    }


    // ---------------------- bubi order line endpoints ----------------------

    @PostMapping("/orderline/save")
    public ResponseEntity<BubiOrderLine> saveBubiOrderLine(@RequestBody BubiOrderLine bubiOrderLine) {
        String vendoraccount = this.bubiDataService.getVendorAccount(bubiOrderLine.getVendorId(), bubiOrderLine.getCollection()).getVendorAccount();
        bubiOrderLine.setVendorAccount(vendoraccount);
        bubiOrderLine.setPrice(this.bubiPricesService.calculatePriceForOrderline(bubiOrderLine));
        bubiOrderLine = this.bubiOrderLineService.saveBubiOrderLine(bubiOrderLine);
        return ResponseEntity.ok(bubiOrderLine);
    }

    @PostMapping("/orderline/updatePrice/{bubiOrderLineId}")
    public ResponseEntity<BubiOrderLine> changePrice(String bubiOrderLineId) {
        BubiOrderLine bubiOrderLine = this.bubiOrderLineService.getBubiOrderLineFromIdentifier(bubiOrderLineId);
        this.bubiPricesService.calculatePriceForOrderline(bubiOrderLine);
        return ResponseEntity.ok(bubiOrderLine);
    }

    @PostMapping("/orderline/changePrice")
    public ResponseEntity<BubiOrderLine> changePrice(@RequestBody BubiOrderLine bubiOrderLine) {
        bubiOrderLine = this.bubiOrderLineService.saveBubiOrderLine(bubiOrderLine);
        this.almaPoLineService.updatePoLineByBubiOrderLine(bubiOrderLine);
        return ResponseEntity.ok(bubiOrderLine);
    }

    @GetMapping("/orderline/fromShelfmark")
    public ResponseEntity<BubiOrderLine> getForShelfmark(String shelfmark, String collection) {
        return ResponseEntity.ok(this.bubiOrderLineService.expandBubiOrderLineFromShelfmark(collection, shelfmark));
    }

    @GetMapping("/orderline/fromBarcode")
    public ResponseEntity<BubiOrderLine> getForBarcode(String barcode) {
        return ResponseEntity.ok(this.bubiOrderLineService.getBubiOrderLineFromBarcode(barcode));
    }

    @GetMapping("/orderline/fromIdentifier")
    public ResponseEntity<BubiOrderLine> getForIdentifier(String identifier) {
        return ResponseEntity.ok(this.bubiOrderLineService.getBubiOrderLineFromIdentifier(identifier));
    }

    @GetMapping("/orderline/retrieve")
    public ResponseEntity<List<BubiOrderLine>> getAllActiveOrderlines(String mode) {
        return ResponseEntity.ok(this.bubiOrderLineService.getOrderLines(mode));

    }

    @GetMapping("/orderline/bubi/{vendorId}")
    public ResponseEntity<List<BubiOrderLine>> getAllOrderlines(@PathVariable String vendorId) {
        return ResponseEntity.ok(this.bubiOrderLineService.getAllBubiOrderLinesForBubi(vendorId));
    }

    @PutMapping("/orderline/changePrice")
    public ResponseEntity<BubiOrder> getAllOrderlines(@RequestBody BubiOrderLine bubiOrderLine) {
        return ResponseEntity.ok(this.bubiOrderService.changePrice(bubiOrderLine));
    }

    // ---------------------- bubi order endpoints ----------------------

    @GetMapping("/orders")
    public ResponseEntity<List<BubiOrder>> getOrders(String mode) {
        return ResponseEntity.ok(this.bubiOrderService.getBubiOrders(mode));
    }

    @GetMapping("/order/retrieve/{bubiOrderId}")
    public ResponseEntity<BubiOrder> getOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.getBubiOrder(bubiOrderId));
    }

    @PostMapping("/order/pack")
    public ResponseEntity<List<BubiOrder>> packOrders(@RequestBody BubiOrder bubiOrder) {
        return ResponseEntity.ok(this.bubiOrderService.packBubiOrder(bubiOrder));
    }

    @PostMapping("/order/collect/{bubiOrderId}")
    public ResponseEntity<BubiOrder> collectOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.collectBubiOrder(bubiOrderId));
    }

    @PostMapping("/order/return/{bubiOrderId}")
    public ResponseEntity<BubiOrder> returnOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.returnBubiOrder(bubiOrderId));
    }

    @PostMapping("/order/pay/{bubiOrderId}")
    public ResponseEntity<BubiOrder> payOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.payBubiOrder(bubiOrderId));
    }

    @PutMapping("/order/removeOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrder> removeOrderLine(String bubiOrderLineid, @RequestBody BubiOrderLine bubiOrderLine) {
        return ResponseEntity.ok(this.bubiOrderService.removeOrderLine(bubiOrderLineid, bubiOrderLine));
    }

    @PutMapping("/order/addOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrder> addOrderLine(String bubiOrderLineid, @RequestBody BubiOrderLine bubiOrderLine) {
        return ResponseEntity.ok(this.bubiOrderService.addOrderLine(bubiOrderLineid, bubiOrderLine));
    }

    @PutMapping("/order/duplicateOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrder> duplicateOrderline(String bubiOrderLineid, @RequestBody BubiOrderLine bubiOrderLine) {
        return ResponseEntity.ok(this.bubiOrderService.duplicateOrderline(bubiOrderLineid, bubiOrderLine));
    }

    // ---------------------- primo data endpoints ----------------------

    @GetMapping("/getJournalData")
    public ResponseEntity<List<AlmaItemData>> getJournalData(String collection, String shelfmark) {
        AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaItemData));
    }
}

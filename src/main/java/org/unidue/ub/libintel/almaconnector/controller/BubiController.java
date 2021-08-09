package org.unidue.ub.libintel.almaconnector.controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unidue.ub.libintel.almaconnector.model.bubi.dto.*;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiPrice;
import org.unidue.ub.libintel.almaconnector.model.bubi.entities.BubiOrder;
import org.unidue.ub.libintel.almaconnector.service.PrimoService;
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

    private final PrimoService primoService;

    BubiController(PrimoService primoService,
                   BubiOrderService bubiOrderService,
                   BubiDataService bubiDataService,
                   CoreDataService coreDataService,
                   BubiOrderLineService bubiOrderLineService,
                   BubiPricesService bubiPricesService) {
        this.primoService = primoService;
        this.bubiOrderService = bubiOrderService;
        this.bubiDataService = bubiDataService;
        this.coreDataService = coreDataService;
        this.bubiOrderLineService = bubiOrderLineService;
        this.bubiPricesService = bubiPricesService;
    }


    // ---------------------- Alma data endpoints ----------------------

    @GetMapping("/almaData")
    private ResponseEntity<List<AlmaItemData>> getAlmaData(String collection, String shelfmark) {
        AlmaItemData almaItemData = new AlmaItemData(collection, shelfmark);
        return ResponseEntity.ok(this.primoService.getPrimoResponse(almaItemData));
    }


    // ---------------------- Bubi data endpoints ----------------------

    @GetMapping("/bubidata")
    private ResponseEntity<List<BubiDataBriefDto>> getbubiData(String mode) {
        return ResponseEntity.ok(this.bubiDataService.listAllBubiData(mode));
    }

    @PostMapping("/bubidata")
    private ResponseEntity<BubiDataFullDto> savebubiData(@RequestBody BubiDataFullDto bubiDataFullDto) {
        return ResponseEntity.ok(this.bubiDataService.saveBubidata(bubiDataFullDto));
    }

    @GetMapping("/bubidata/retrieve")
    private ResponseEntity<BubiDataFullDto> getBubiData(String bubidataId) {
        return ResponseEntity.ok(this.bubiDataService.getBubiData(bubidataId));
    }

    @PostMapping("/bubidata/active/{bubidataId}")
    private ResponseEntity<BubiDataFullDto> toggleActive(@PathVariable String bubidataId) {
        return ResponseEntity.ok(this.bubiDataService.toggleActive(bubidataId));
    }


    // ---------------------- Bubi prices endpoints ----------------------

    @PostMapping("/prices")
    private ResponseEntity<BubiPrice> saveBubiPrice(@RequestBody BubiPrice bubiPrice) {
        return ResponseEntity.ok(this.bubiPricesService.saveBubiPrice(bubiPrice));
    }

    @DeleteMapping("/prices/delete")
    private ResponseEntity<?> deleteBubiPrice(String vendorAccount) {
        this.bubiPricesService.deleteBubiPrices(vendorAccount);
        return ResponseEntity.ok().build();
    }


    // ---------------------- Core data endpoints ----------------------

    @GetMapping("/coredata")
    private ResponseEntity<List<CoreDataBriefDto>> getCoreData(String mode) {
        return ResponseEntity.ok(this.coreDataService.getCoreData(mode));
    }

    @GetMapping("/coredata/retrieve")
    public ResponseEntity<CoreDataFullDto> getCoreDatum(String coredataId) {
        return ResponseEntity.ok(this.coreDataService.getCoreDatum(coredataId));
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

    @PostMapping("/coredata")
    public ResponseEntity<CoreDataFullDto> saveCoredata(@RequestBody CoreDataFullDto coreDataFullDto) {
        return ResponseEntity.ok(this.coreDataService.saveCoreData(coreDataFullDto));
    }

    @DeleteMapping("/coredata/delete")
    public ResponseEntity<?> deleteCoreData(String collection, String shelfmark) {
        this.coreDataService.deleteCoreData(collection + "-" + shelfmark);
        return ResponseEntity.ok().build();
    }


    // ---------------------- bubi order line endpoints ----------------------

    @GetMapping("/orderline")
    public ResponseEntity<List<BubiOrderLineBriefDto>> getOrderlines(String mode) {
        return ResponseEntity.ok(this.bubiOrderLineService.getOrderLines(mode));
    }

    @PostMapping("/orderline")
    public ResponseEntity<BubiOrderLineFullDto> saveBubiOrderLine(@RequestBody BubiOrderLineFullDto bubiOrderLineFullDto) {
        return ResponseEntity.ok(new BubiOrderLineFullDto(this.bubiOrderLineService.saveBubiOrderLineFullDTO(bubiOrderLineFullDto)));
    }

    @GetMapping("/orderline/fromShelfmark")
    public ResponseEntity<BubiOrderLineFullDto> getForShelfmark(String shelfmark, String collection) {
        return ResponseEntity.ok(new BubiOrderLineFullDto(this.bubiOrderLineService.expandBubiOrderLineFromShelfmark(collection, shelfmark)));
    }

    @GetMapping("/orderline/fromBarcode")
    public ResponseEntity<BubiOrderLineFullDto> getForBarcode(String barcode) {
        return ResponseEntity.ok(new BubiOrderLineFullDto(this.bubiOrderLineService.getBubiOrderLineFromBarcode(barcode)));
    }

    @GetMapping("/orderline/fromCoredata")
    public ResponseEntity<BubiOrderLineFullDto> getForCoredata(String coredataId) {
        return ResponseEntity.ok(new BubiOrderLineFullDto(this.bubiOrderLineService.getBubiOrderLineFromCoredata(coredataId)));
    }

    @GetMapping("/orderline/fromIdentifier")
    public ResponseEntity<BubiOrderLineFullDto> getForIdentifier(String identifier) {
        return ResponseEntity.ok(this.bubiOrderLineService.getBubiOrderLineFromIdentifier(identifier));
    }

    @GetMapping("/orderline/bubi")
    public ResponseEntity<List<BubiOrderLineBriefDto>> getAllOrderlines( String vendorAccount) {
        return ResponseEntity.ok(this.bubiOrderLineService.getAllBubiOrderLinesForBubi(vendorAccount));
    }


    // ---------------------- bubi order endpoints ----------------------

    @GetMapping("/order")
    public ResponseEntity<List<BubiOrderBriefDto>> getOrders(String mode) {
        return ResponseEntity.ok(this.bubiOrderService.getBriefBubiOrders(mode));
    }

    @GetMapping("/order/list")
    public ResponseEntity<List<BubiOrderShortDto>> getOrderOverview(String mode) {
        return ResponseEntity.ok(this.bubiOrderService.getShortBubiOrders(mode));
    }

    @GetMapping("/order/retrieve")
    public ResponseEntity<BubiOrderFullDto> getOrder(String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.getBubiOrderFull(bubiOrderId));
    }

    @PostMapping("/order/pack")
    public ResponseEntity<List<BubiOrderFullDto>> packOrders(@RequestBody BubiOrder bubiOrder) {
        return ResponseEntity.ok(this.bubiOrderService.packBubiOrder(bubiOrder));
    }

    @PostMapping("/order/collect/{bubiOrderId}")
    public ResponseEntity<BubiOrderFullDto> collectOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.collectBubiOrder(bubiOrderId));
    }

    @PostMapping("/order/return/{bubiOrderId}")
    public ResponseEntity<BubiOrderFullDto> returnOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.returnBubiOrder(bubiOrderId));
    }

    @PostMapping("/order/pay/{bubiOrderId}")
    public ResponseEntity<BubiOrderFullDto> payOrder(@PathVariable String bubiOrderId) {
        return ResponseEntity.ok(this.bubiOrderService.payBubiOrder(bubiOrderId));
    }

    @PutMapping("/order/removeOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrderFullDto> removeOrderLine(@PathVariable String bubiOrderLineid, String bubiOrderLineId) {
        return ResponseEntity.ok(this.bubiOrderService.removeOrderLine(bubiOrderLineid, bubiOrderLineId));
    }

    @PutMapping("/order/addOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrderFullDto> addOrderLine(@PathVariable String bubiOrderLineid, String bubiOrderLineId) {
        return ResponseEntity.ok(this.bubiOrderService.addOrderLine(bubiOrderLineid, bubiOrderLineId));
    }

    @PutMapping("/order/duplicateOrderline/{bubiOrderLineid}")
    public ResponseEntity<BubiOrderFullDto> duplicateOrderline(@PathVariable String bubiOrderLineid, String bubiOrderLineId) {
        return ResponseEntity.ok(this.bubiOrderService.duplicateOrderline(bubiOrderLineid, bubiOrderLineId));
    }
}

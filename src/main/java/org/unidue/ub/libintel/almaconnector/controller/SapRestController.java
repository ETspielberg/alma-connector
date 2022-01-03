package org.unidue.ub.libintel.almaconnector.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.unidue.ub.libintel.almaconnector.model.run.SapDataRun;
import org.unidue.ub.libintel.almaconnector.model.run.SapResponseRun;
import org.unidue.ub.libintel.almaconnector.model.sap.AvailableInvoice;
import org.unidue.ub.libintel.almaconnector.service.SapService;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;

import java.io.IOException;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.service.SapService.getFromExcel;

@RestController
@RequestMapping("/sap/api/v1/")
@Slf4j
public class SapRestController {

    private final SapService sapService;

    private final AlmaInvoiceService almaInvoiceService;

    /**
     * constructor based autowiring to the invoice service, the filewriter service and the vendorservice.
     *
     * @param sapService          the sap service
     */
    SapRestController(SapService sapService,
                      AlmaInvoiceService almaInvoiceService) {
        this.sapService = sapService;
        this.almaInvoiceService = almaInvoiceService;
    }

    @GetMapping("availableInvoices/{invoiceOwner}")
    public ResponseEntity<List<AvailableInvoice>> getAvailableInvoices(@PathVariable String invoiceOwner) {
        return ResponseEntity.ok(this.almaInvoiceService.getAvailableInvoices(invoiceOwner));

    }

    @GetMapping("updateInvoice/{invoiceNumber}")
    public ResponseEntity<AvailableInvoice> updateAvailableInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(new AvailableInvoice(this.almaInvoiceService.retrieveInvoice(invoiceNumber)));
    }

    @GetMapping("sapDataRun/{invoiceOwner}")
    public ResponseEntity<SapDataRun> getAlmaExportRun(@PathVariable String invoiceOwner) {
        SapDataRun sapDataRun = new SapDataRun(invoiceOwner);
        this.sapService.addInvoices(sapDataRun);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }

    @GetMapping("reloadSingleInvoice/{invoiceNumber}")
    public ResponseEntity<SapDataRun> getSingleInvoiceData(@PathVariable String invoiceNumber) {
        SapDataRun sapDataRun = new SapDataRun(invoiceNumber);
        this.sapService.addSingleInvoice(sapDataRun);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }

    @PostMapping("prepareInputFiles")
    public ResponseEntity<String> prepareInputFiles(@RequestBody SapDataRun sapDataRun) {
        try {
            this.sapService.writeExportFiles(sapDataRun);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("collectSapDataForInvoices")
    public  ResponseEntity<SapDataRun> collectSapDataForInvoices(@RequestBody List<AvailableInvoice> invoices) {
        SapDataRun sapDataRun = this.sapService.addManualInvoices(invoices);
        this.sapService.addSapData(sapDataRun);
        return ResponseEntity.ok(sapDataRun);
    }

    /**
     * receives the sap import result as xlsx file and updates the invoices in alma correspondingly
     * @param sapReturnFile the result xlsx file resulting from the SAP import
     * @return returns a status of 200 if the import was successful
     */
    @PostMapping("/invoicesUpdate")
    public ResponseEntity<SapResponseRun> updateInvoicesWithSapData(@RequestParam("file") MultipartFile sapReturnFile) {
        try {// read the excel spreadsheet from the request
            XSSFWorkbook workbook = new XSSFWorkbook(sapReturnFile.getInputStream());
            // retrieve first sheet
            XSSFSheet worksheet = workbook.getSheetAt(0);
            //convert the excel sheet to a SapResponseRun holding the individual responses
            SapResponseRun container = getFromExcel(worksheet, sapReturnFile.getOriginalFilename());
            return ResponseEntity.ok(this.sapService.updateInvoiceWithErpData(container));
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file could not be read from request", ioe);
        }
    }


}

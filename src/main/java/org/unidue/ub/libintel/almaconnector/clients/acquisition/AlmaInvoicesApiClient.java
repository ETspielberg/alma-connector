package org.unidue.ub.libintel.almaconnector.clients.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.InvoiceLine;
import org.unidue.ub.alma.shared.acq.Invoices;

import java.util.List;

@FeignClient(name = "invoices", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/acq/invoices", configuration = AcquisitionFeignConfiguration.class)
@Service
public interface AlmaInvoicesApiClient {


    /**
     * Get Invoices
     * This API returns a list of  Invoices.
     * @param baseStatus Invoice base status. Possible values are ACTIVE, CLOSED (required)
     * @param invoiceWorkflowStatus Invoice workflow status. Possible codes are listed in &#39;InvoiceStatus&#39; [code table](https://developers.exlibrisgroup.com/blog/Working-with-the-code-tables-API) (required)
     * @param owner Invoice owner. Can be the institution code or a library code. See [Get libraries API](https://developers.exlibrisgroup.com/alma/apis/conf/GET/gwPcGly021p29HpB7XTI4Dp4I8TKv6CAxBlD4LyRaVE&#x3D;/37088dc9-c685-4641-bc7f-60b5ca7cabed). (required)
     * @param creationForm Invoice creation form. Possible codes are listed in &#39;InvoiceCreationForm&#39; [code table](https://developers.exlibrisgroup.com/blog/Working-with-the-code-tables-API) (required)
     * @param q Search query. Optional. Searched fields: invoice_number, vendor_code, additional_pol_reference, invoice_reference_number, pol_number, pol_title, po_number, vendor_name, all. Note that some combinations of search fields cannot be used together. (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;all&quot;)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @param view Invoice view. If view&#x3D;brief, invoices will be returned without lines. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET, value="/")
    Invoices getInvoices(@RequestHeader("Accept") String accept, @RequestParam("base_status") String baseStatus, @RequestParam("invoice_workflow_status") String invoiceWorkflowStatus, @RequestParam("owner") String owner, @RequestParam("creation_form") String creationForm, @RequestParam("q") String q, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset, @RequestParam("view") String view);


    /**
     * Get Invoice
     * This API returns a specific Invoice.
     * @param invoiceId The Invoice id. (required)
     * @param view Invoice view. If view&#x3D;brief, invoices will be returned without lines. (optional, default to &quot;&quot;)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET, value="/{invoiceId}")
     Invoice getInvoicesInvoiceId(@RequestHeader("Accept") String accept, @PathVariable("invoice_id") String invoiceId, @RequestParam("view") String view);

    /**
     * Get Invoice Lines
     * This API returns a specific Invoice&#39;s lines.
     * @param invoiceId The Invoice id. (required)
     * @param q Search query. Optional. Searching for fields: invoice_line_number. Example (note the tilde between the code and text): q&#x3D;invoice_line_number~101 (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET, value="/{invoiceId}/lines")
    List<InvoiceLine> getInvoicesInvoiceIdLines(@RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId, @RequestParam("q") String q, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset);

    /**
     * Get Invoice Line
     * This API returns a specific Invoice&#39;s specific Invoice line.
     * @param invoiceId The Invoice id. (required)
     * @param invoiceLineId The Invoice line id. (required)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.GET, value="{invoiceId}/lines/{invoiceLineId}")
    InvoiceLine getInvoicesInvoiceIdLinesInvoiceLineId(@RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId, @PathVariable("invoiceLineId") String invoiceLineId);

    /**
     * Create Invoice
     * This API creates an invoice. See blog: [Creating an invoice using APIs](https://developers.exlibrisgroup.com/blog/Creating-an-invoice-using-APIs).
     * @param body This method takes an invoice object. See [here](/alma/apis/docs/xsd/rest_invoice.xsd?tags&#x3D;POST) (required)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST)
    Invoice postAcqInvoices(@RequestBody Invoice body, @RequestHeader("Accept") String accept);

    /**
     * Invoice Service
     * This API operates on an invoice.  It is possible to use this API to process an invoice - see blog: [Creating an invoice using APIs](https://developers.exlibrisgroup.com/blog/Creating-an-invoice-using-APIs).   It is also possible to use this API for invoice integration with ERP system - see blog [Alma - ERP invoices integration using APIs](https://developers.exlibrisgroup.com/blog/Alma-ERP-invoices-integration-using-APIs).
     * @param invoiceId The Invoice id. (required)
     * @param op The operation to perform on the invoice. Currently, the options are &#39;mark_in_erp&#39;, &#39;paid&#39;, &#39;process_invoice&#39; or &#39;rejected&#39; (required)
     * @param body This method takes an invoice object. See [here](/alma/apis/docs/xsd/rest_invoice.xsd?tags&#x3D;POST) (required)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST, value="/{invoiceId}")
    Invoice postInvoicesInvoiceId(@RequestBody Invoice body, @RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId, @RequestParam("op") String op);

    /**
     * Create Invoice Line
     * This API creates an invoice line. See blog: [Creating an invoice using APIs](https://developers.exlibrisgroup.com/blog/Creating-an-invoice-using-APIs).
     * @param invoiceId The Invoice id. (required)
     * @param body This method takes an invoice line object. See [here](/alma/apis/docs/xsd/rest_invoice_line.xsd?tags&#x3D;POST) (required)
     * @return Object
     */
    @RequestMapping(method=RequestMethod.POST, value="/{invoiceId}/lines")
    InvoiceLine postInvoicesInvoiceIdLines(@RequestBody InvoiceLine body, @RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId);

    /**
     * Update Invoice
     * This API updates an invoice.
     * @param invoiceId The Invoice id. (required)
     * @param body This method takes an invoice object. See [here](/alma/apis/docs/xsd/rest_invoice.xsd?tags&#x3D;PUT) (required)
     * @return Object
     */
    @RequestMapping(method= RequestMethod.PUT, value="/{invoiceId}")
    Invoice putInvoicesInvoiceId(@RequestBody Invoice body, @RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId);

    /**
     * Update Invoice Line
     * This API updates an invoice line.
     * @param invoiceId The Invoice id. (required)
     * @param invoiceLineId The Invoice line id. (required)
     * @param body This method takes an invoice line object. See [here](/alma/apis/docs/xsd/rest_invoice_line.xsd?tags&#x3D;PUT) (required)
     * @return Object
     */
    @RequestMapping(method= RequestMethod.PUT, value="/{invoiceId}/lines/{invoiceLineId}")
    InvoiceLine putInvoicesInvoiceIdLinesInvoiceLineId(@RequestBody InvoiceLine body, @RequestHeader("Accept") String accept, @PathVariable("invoiceId") String invoiceId, @PathVariable("invoiceLineId") String invoiceLineId);
}

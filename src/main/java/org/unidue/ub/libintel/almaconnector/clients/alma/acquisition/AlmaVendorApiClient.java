package org.unidue.ub.libintel.almaconnector.clients.alma.acquisition;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.alma.shared.acq.PoLine;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

import java.util.List;

@FeignClient(name = "vendors", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/acq/vendors", configuration = AlmaFeignConfiguration.class)
public interface AlmaVendorApiClient {

    /**
     * Delete Vendor
     * This API deletes the vendor the code refers to.
     * @param vendorCode Specific vendor code (required)
     */
    @RequestMapping(method= RequestMethod.DELETE,
            value="/{vendorCode}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    void deleteVendorsVendorCode(@PathVariable("vendorCode") String vendorCode);

    /**
     * Retrieve vendors
     * This API returns a list of Vendors.
     * @param status Vendor Status. Optional. Valid values: active, inactive (optional, default to &quot;ALL&quot;)
     * @param type Vendor Type. Optional. Valid values: material_supplier, access_provider, licensor, governmental. (optional, default to &quot;ALL&quot;)
     * @param q Search query. Optional. Searching for words from: interface_name, name, code, library  &amp; all (for searching in all the above fields). Example (note the tilde between the code and text): q&#x3D;name~Association (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @return List<Vendor>
     */
    @RequestMapping(method= RequestMethod.GET,
            value="",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<Vendor> getVendors(@RequestParam("status") String status,
                            @RequestParam("type") String type,
                            @RequestParam("q") String q,
                            @RequestParam("limit") Integer limit,
                            @RequestParam("offset") Integer offset);

    /**
     * Get Vendor
     * This API returns a specific vendor information.
     * @param vendorCode Specific vendor code (required)
     * @return Vendor
     */
    @RequestMapping(method= RequestMethod.GET,
            value="/{vendorCode}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Vendor getVendorsVendorCode(@PathVariable("vendorCode") String vendorCode);

    /**
     * Get Vendor Invoices
     * This API returns information about a specific vendor&#39;s invoices.
     * @param vendorCode Specific vendor code (required)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @return Object
     */
    @RequestMapping(method= RequestMethod.GET,
            value="/{vendorCode}/invoices",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<Invoice> getVendorsVendorCodeInvoices(@PathVariable("vendorCode") String vendorCode,
                                               @RequestParam("limit") Integer limit,
                                               @RequestParam("offset") Integer offset);

    /**
     * Get Vendor PO Lines
     * This API returns information about a specific vendor&#39;s PO lines.
     * @param vendorCode Specific vendor code (required)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @return List<PoLine>
     */
    @RequestMapping(method= RequestMethod.GET,
            value="/{vendorCode}/po-lines",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<PoLine> getVendorsVendorCodePoLines(@PathVariable("vendorCode") String vendorCode,
                                             @RequestParam("limit") Integer limit,
                                             @RequestParam("offset") Integer offset);

    /**
     * Create Vendor
     * This API creates a new vendor.
     * @param body This method takes a Vendor object. See [here](/alma/apis/docs/xsd/rest_vendor.xsd?tags&#x3D;POST) (required)
     * @return Vendor
     */
    @RequestMapping(method= RequestMethod.POST,
            value="",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Vendor postVendors(@RequestBody Vendor body);

    /**
     * Update Vendor
     * This API updates an existing vendor.
     * @param vendorCode Specific vendor code (required)
     * @param body This method takes a Vendor object. See [here](/alma/apis/docs/xsd/rest_vendor.xsd?tags&#x3D;PUT) (required)
     * @return Vendor
     */
    @RequestMapping(method= RequestMethod.PUT,
            value="/{vendorCode}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Vendor putVendorsVendorCode(@RequestBody Vendor body,
                                @PathVariable("vendorCode") String vendorCode);
}

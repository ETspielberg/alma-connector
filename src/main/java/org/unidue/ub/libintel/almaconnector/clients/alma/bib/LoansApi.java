package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.ItemLoan;
import org.unidue.ub.alma.shared.user.UserLoans;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "loans", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface LoansApi {


    /**
     * Loan By Item information
     * This web service returns Item Loan by Item information.
     *
     * @param mmsId     The Bib Record ID. (required)
     * @param holdingId The Holding Record ID. (required)
     * @param itemId    The Item ID. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/{mmsId}/holdings/{holdingId}/items/{itemId}/loans",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    UserLoans getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemIdLoans(@PathVariable String mmsId,
                                                                    @PathVariable String holdingId,
                                                                    @PathVariable String itemId);

    /**
     * Retrieve Item Loan information
     * This web service returns Item Loan information.
     *
     * @param mmsId     The Bib Record ID. (required)
     * @param holdingId The Holding Record ID. (required)
     * @param itemPid   The Item ID. (required)
     * @param loanId    The Loan ID. (required)
     * @return Object
     */
    @RequestLine("GET /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}")
    @Headers({
            "Accept: application/json",
    })
    @RequestMapping(method=RequestMethod.GET,
            value = "/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ItemLoan getAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@PathVariable String mmsId,
                                                                          @PathVariable String holdingId,
                                                                          @PathVariable String itemPid,
                                                                          @PathVariable String loanId);

    /**
     * Retrieve Bib Loan information
     * This web service returns Loan information for a Bib record.
     *
     * @param mmsId The Bib Record ID. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/{mmsId}/loans",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    UserLoans getAlmawsV1BibsMmsIdLoans(@PathVariable String mmsId);

    /**
     * Retrieve Bib Loan information for a Bib id and Loan id
     * This Web service retrieves loan information for a particular Bib id and Loan id.
     *
     * @param mmsId  The Bib Record ID. (required)
     * @param loanId The Loan ID. (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/{mmsId}/loans/{loanId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Object getAlmawsV1BibsMmsIdLoansLoanId(@PathVariable String mmsId,
                                           @PathVariable String loanId);

    /**
     * Create user loan
     * This web service loans an item to a user. The loan will be created according to the library&#39;s policy.
     *
     * @param mmsId      The Bib Record ID. (required)
     * @param holdingId  The Holding Record ID. (required)
     * @param itemPid    The Item ID. (required)
     * @param userId     A unique identifier of the loaning user. Mandatory. (required)
     * @param body       This method takes a Loan object. See [here](/alma/apis/docs/xsd/rest_item_loan.xsd?tags&#x3D;POST) (required)
     * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in the User Identifier Type code table. (optional, default to &quot;all_unique&quot;)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.POST,
            value = "/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ItemLoan postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoans(@PathVariable String mmsId,
                                                                     @PathVariable String holdingId,
                                                                     @PathVariable String itemPid,
                                                                     @RequestParam("user_id") String userId,
                                                                     @RequestBody ItemLoan body,
                                                                     @RequestParam("user_id_type") String userIdType);


    /**
     * Action on a loan
     * This Web service performs an action on a loan.
     *
     * @param mmsId     The Bib Record ID. (required)
     * @param holdingId The Holding Record ID. (required)
     * @param itemPid   The Item ID. (required)
     * @param loanId    The Loan ID. (required)
     * @param op        Operation. Currently only op&#x3D;renew is supported (required)
     * @return Object
     */
    @RequestMapping(method = RequestMethod.POST,
            value = "/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ItemLoan postAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@PathVariable String mmsId,
                                                                           @PathVariable String holdingId,
                                                                           @PathVariable String itemPid,
                                                                           @PathVariable String loanId,
                                                                           @RequestParam("op") String op);


    /**
     * Change loan due date
     * This Web service changes a loan due date.
     *
     * @param mmsId     The Bib Record ID. (required)
     * @param holdingId The Holding Record ID. (required)
     * @param itemPid   The Item ID. (required)
     * @param loanId    The Loan ID. (required)
     * @param body      This method takes an item loan object See [here](/alma/apis/docs/xsd/rest_item_loan.xsd?tags&#x3D;PUT) (required)
     * @return Object
     */
    @RequestLine("PUT /almaws/v1/bibs/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}")
    @Headers({
            "Content-Type: application/json",
            "Accept: application/json",
    })
    @RequestMapping(method = RequestMethod.PUT,
            value = "/{mmsId}/holdings/{holdingId}/items/{itemPid}/loans/{loanId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ItemLoan putAlmawsV1BibsMmsIdHoldingsHoldingIdItemsItemPidLoansLoanId(@PathVariable String mmsId,
                                                                        @PathVariable String holdingId,
                                                                        @PathVariable String itemPid,
                                                                        @PathVariable String loanId,
                                                                        @RequestBody ItemLoan body);
}

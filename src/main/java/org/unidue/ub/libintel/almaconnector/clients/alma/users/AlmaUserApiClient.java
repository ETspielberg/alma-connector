package org.unidue.ub.libintel.almaconnector.clients.alma.users;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.user.AlmaUser;
import org.unidue.ub.alma.shared.user.UserLoans;
import org.unidue.ub.alma.shared.user.UserRequests;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

import java.util.List;

@FeignClient(name="almaUser", url="https://api-eu.hosted.exlibrisgroup.com/almaws/v1/users", configuration= AlmaFeignConfiguration.class)
@Service
public interface AlmaUserApiClient {

    /**
     * Delete user
     * This Web service deletes a specific user.
     * @param userId A unique identifier for the user (required)
     * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. (optional, default to &quot;all_unique&quot;)
     */
    @RequestMapping(method=RequestMethod.DELETE, value="/{userId}")
    void deleteAlmaUsersUserId(@PathVariable("userId") String userId,
                               @RequestParam("user_id_type") String userIdType);

    /**
     * Retrieve users
     * This API returns a list of Users, sorted by last name.
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @param q Search query. Optional. Searching for words from: primary_id, first_name, last_name, middle_name, email, job_category, identifiers, general_info and ALL. Example (note the tilde between the code and text): q&#x3D;last_name~Smith (see [Brief Search](https://developers.exlibrisgroup.com/blog/How-we-re-building-APIs-at-Ex-Libris#BriefSearch)) (optional, default to &quot;&quot;)
     * @param orderBy A few sort options are available: last_name, first_name and primary_id. One sort option may be used at a time. A secondary sort key, primary_id, is added if last_name or first_name is the primary sort. Default sorting is by all three in the following order: last_name, first_name, primary_id. If the query option is used, the result will not sort by primary_id. (optional, default to &quot;last_name, first_name, primary_id&quot;)
     * @param sourceInstitutionCode The code of the source institution from which the user was linked. Optional (optional, default to &quot;&quot;)
     * @param sourceUserId The ID of the user in the source institution. Optional. (optional, default to &quot;&quot;)
     * @return List<AlmaUser>
     */
    @RequestMapping(method=RequestMethod.GET, value="")
    List<AlmaUser> getAlmaUsers(@RequestParam("limit") Integer limit,
                                @RequestParam("offset") Integer offset,
                                @RequestParam("q") String q,
                                @RequestParam("order_by") String orderBy,
                                @RequestParam("source_institution_code") String sourceInstitutionCode,
                                @RequestParam("source_user_id") String sourceUserId);

    /**
     * Get user details
     * This Web service returns a specific user&#39;s details.
     * @param userId A unique identifier for the user (required)
     * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. The value may also be linking_id.  To search for users which have linked accounts in other institutions according to the linking_id use user_id_type&#x3D;linking_id. (optional, default to &quot;all_unique&quot;)
     * @param view Special view of User object. Optional. Possible values: full - full User object will be returned. brief - only user&#39;s core information, emails, identifiers and statistics are returned. By default, the full User object will be returned. (optional, default to &quot;full&quot;)
     * @param expand This parameter allo for expanding on some user information. Three options are available: loans-Include the total number of loans; requests-Include the total number of requests; fees-Include the balance of fees. To have more than one option, use a comma separator. (optional, default to &quot;none&quot;)
     * @param sourceInstitutionCode The source institution Code. Optional. When used the user_id is used to locate a copied user (linked account) based on source_link_id. (optional, default to &quot;&quot;)
     * @return AlmaUser
     */
    @RequestMapping(method=RequestMethod.GET, value="/{userId}")
    AlmaUser getAlmaUsersUserId(@PathVariable("userId") String userId,
                                @RequestParam("user_id_type") String userIdType,
                                @RequestParam("view") String view,
                                @RequestParam("expand") String expand,
                                @RequestParam("source_institution_code") String sourceInstitutionCode);

    @RequestMapping(method=RequestMethod.GET, value="/{userId}")
    AlmaUser getAlmaUser(@PathVariable("userId") String userId,
                         @RequestParam("view") String view);

    /**
     * Create user
     * This Web service creates a new user.
     * @param body This method takes a User object. See [here](/alma/apis/docs/xsd/rest_user.xsd?tags&#x3D;POST) (required)
     * @param socialAuthentication When customer parameter social_authentication&#x3D;&#39;True&#39;: Send social authentication email to patron. Default value: False. (optional, default to &quot;false&quot;)
     * @param sendPinNumberLetter The email notification for PIN setting change will be sent (optional, default to &quot;false&quot;)
     * @param sourceInstitutionCode The code of the source institution from which the user was linked. Optional (optional, default to &quot;&quot;)
     * @param sourceUserId The ID of the user in the source institution. Optional. (optional, default to &quot;&quot;)
     * @return AlmaUser
     */
    @RequestMapping(method=RequestMethod.POST, value="")
    AlmaUser postAlmaUsers(@RequestBody AlmaUser body,
                           @RequestParam("social_authentication") String socialAuthentication,
                           @RequestParam("send_pin_number_letter") String sendPinNumberLetter,
                           @RequestParam("source_institution_code") String sourceInstitutionCode,
                           @RequestParam("source_user_id") String sourceUserId);

    /**
     * Authenticate or refresh user
     * This Web service runs a user authentication process or refreshes a linked user in Alma.   Refresh operation requires a user at the local institution that is linked to a user at another institution.  Authentication operation requires a password which may be entered as a parameter or with the header: Exl-User-Pw  Authentication is meant for internal users that have passwords in the Ex Libris Identity Service.  Successful authentication or refresh will result with an HTTP 204 (success - no content) response.
     * @param userId A unique identifier for the user (required)
     * @param password Add the user&#39;s password. Or, enter the password in the header Exl-User-Pw. A password is mandatory for op&#x3D;auth. (required)
     * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. (optional, default to &quot;all_unique&quot;)
     * @param op The operation to be performed on the user. Mandatory. Currently op&#x3D;auth or op&#x3D;refresh are supported.  The default is auth. (optional, default to &quot;auth&quot;)
     * @return AlmaUser
     */
    @RequestMapping(method=RequestMethod.POST, value="/{userId}")
    AlmaUser postAlmaUsersUserId(@PathVariable("userId") String userId,
                                 @RequestParam("password") String password,
                                 @RequestParam("user_id_type") String userIdType,
                                 @RequestParam("op") String op);

    /**
     * Update User Details
     * This Web service updates a specific user&#39;s details.     The update is done in a &#39;Swap All&#39; mode: existing fields&#39; information will be replaced with the incoming information. Incoming lists will replace existing lists.   Exception for this are the following fields:   roles - if the incoming list does not contain roles, exsiting roles will be kept.    External users: User group, Job category, PIN number, User language, Resource sharing libraries, Campus code and User title: these fields will not be replaced if updated manually (or if empty in the incoming user record), unless &#39;override&#39; parameter is sent with the field&#39;s name.
     * @param userId A unique identifier for the user (required)
     * @param body This method takes a User object. See [here](/alma/apis/docs/xsd/rest_user.xsd?tags&#x3D;PUT) (required)
     * @param userIdType The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. (optional, default to &quot;all_unique&quot;)
     * @param override The following fields of the user object are not replaced if they were updated manually:   user_group, job_category, pin_number, preferred_language, campus_code, rs_libraries, user_title, library_notices.   To update these fields, specify the fields you want to replace in this parameter.   For example override&#x3D;user_group,job_category. Default is empty. (optional, default to &quot;&quot;)
     * @param sendPinNumberLetter The email notification for PIN setting change will be sent (optional, default to &quot;false&quot;)
     * @return AlmaUser
     */
    @RequestMapping(method=RequestMethod.PUT, value="/{userId}")
    AlmaUser putAlmaUsersUserId(@RequestBody AlmaUser body,
                                @PathVariable("userId") String userId,
                                @RequestParam("user_id_type") String userIdType,
                                @RequestParam("override") String override,
                                @RequestParam("send_pin_number_letter") String sendPinNumberLetter);

    /**
     * Get user loans
     * @param userId A unique identifier for the user (required)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @param user_id_type The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. The value may also be linking_id.  To search for users which have linked accounts in other institutions according to the linking_id use user_id_type&#x3D;linking_id. (optional, default to &quot;all_unique&quot;)
     * @param order_by A few sort options are available: last_name, first_name and primary_id. One sort option may be used at a time. A secondary sort key, primary_id, is added if last_name or first_name is the primary sort. Default sorting is by all three in the following order: last_name, first_name, primary_id. If the query option is used, the result will not sort by primary_id. (optional, default to &quot;last_name, first_name, primary_id&quot;)
     * @param direction Sorting direction: ASC/DESC. Default: ASC
     * @param expand Comma separated list of values for expansion of results. Possible values: 'renewable'
     * @param loan_status Active or Completeloan status. Default: Active. The Complete loan status is only relevant if historic loans haven't been anonymized
     * @return UserLoans
     */
    @RequestMapping(method=RequestMethod.GET, value="/{userId}/loans")
    UserLoans getUserLoansByUserId(@PathVariable String userId,
                                   @RequestParam int limit,
                                   @RequestParam int offset,
                                   @RequestParam String user_id_type,
                                   @RequestParam String order_by,
                                   @RequestParam String direction,
                                   @RequestParam String expand,
                                   @RequestParam String loan_status);

    /**
     *
     * @param userId A unique identifier for the user (required)
     * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
     * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
     * @param user_id_type The type of identifier that is being searched. Optional. If this is not provided, all unique identifier types are used. The values that can be used are any of the values in UserIdentifierTypes code table. The value may also be linking_id.  To search for users which have linked accounts in other institutions according to the linking_id use user_id_type&#x3D;linking_id. (optional, default to &quot;all_unique&quot;)
     * @param request_type Filter results by request type. Optional. Possible values: HOLD, DIGITIZATION, BOOKING. If not supplied, all request types will be returned.
     * @param status Active or History request status. Default is active. The 'history' option is only available if the 'should_anonymize_requests' customer parameter is set to 'false' at the time the request was completed
     * @return UserRequests
     */
    @RequestMapping(method=RequestMethod.GET, value="/{userId}/requests")
    UserRequests getUserRequestsByUserId(@PathVariable String userId,
                                         @RequestParam int limit,
                                         @RequestParam int offset,
                                         @RequestParam String request_type,
                                         @RequestParam String user_id_type,
                                         @RequestParam String status);
}

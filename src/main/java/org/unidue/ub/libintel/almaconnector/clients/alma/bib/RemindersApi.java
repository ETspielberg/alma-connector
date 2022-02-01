package org.unidue.ub.libintel.almaconnector.clients.alma.bib;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "reminders", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/bibs", configuration = AlmaFeignConfiguration.class)
@Service
public interface RemindersApi {


  /**
   * Delete a Reminder for BIB
   * This Web service deletes a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   */
  @RequestMapping(method = RequestMethod.DELETE,
          value = "/{mmsId}/reminders/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteBibsMmsIdRemindersReminderId(@PathVariable("mmsId") String mmsId,
                                                  @PathVariable("reminderId") String reminderId);

  /**
   * Retrieve a list of Reminders for BIB
   * This Web service returns a list of Reminders for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param type Type for filtering. Optional. Valid values are from the ReminderTypes code table (optional, default to &quot;&quot;)
   * @param status Status for filtering. Optional. Valid values are from the ReminderStatuses code table (optional, default to &quot;&quot;)
   * @param from From this Date (YYYY-MM-DD). Optional. Defaults to today. (optional, default to &quot;&quot;)
   * @param to To this Date (YYYY-MM-DD). Optional. Defaults to the From Date. (optional, default to &quot;&quot;)
   * @param orderBy Order by parameter. Optional. Valid values are type, status, reminder_date. (optional, default to &quot;type&quot;)
   * @param direction Direction parameter. Optional. Valid values are asc, desc. (optional, default to &quot;asc&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/reminders",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdReminders(@PathVariable("mmsId") String mmsId,
                                       @RequestParam("type") String type,
                                       @RequestParam("status") String status,
                                       @RequestParam("from") String from,
                                       @RequestParam("to") String to,
                                       @RequestParam("order_by") String orderBy,
                                       @RequestParam("direction") String direction,
                                       @RequestParam("limit") Integer limit,
                                       @RequestParam("offset") Integer offset);


  /**
   * Retrieve a Reminder for BIB
   * This Web service returns a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.GET,
          value = "/{mmsId}/reminders/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getBibsMmsIdRemindersReminderId(@PathVariable("mmsId") String mmsId,
                                                 @PathVariable("reminderId") String reminderId);

  /**
   * Creates a Reminder for BIB
   * This Web service creates a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.POST,
          value = "/{mmsId}/reminders",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postBibsMmsIdReminders(@PathVariable("mmsId") String mmsId,
                                        @RequestBody Object body);

  /**
   * Update a Reminder for BIB
   * This Web service updates a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method = RequestMethod.PUT,
          value = "/{mmsId}/reminders/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putBibsMmsIdRemindersReminderId(@PathVariable("mmsId") String mmsId,
                                                 @PathVariable("reminderId") String reminderId,
                                                 @RequestBody Object body);
}

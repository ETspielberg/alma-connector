package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "reminders", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/reminders", configuration = AlmaFeignConfiguration.class)
@Service
public interface RemindersApi {


  /**
   * Delete a Reminder
   * This Web service deletes a Reminder.  
   * @param reminderId Reminder ID. Required. (required)
   */
  @RequestMapping(method= RequestMethod.GET,
          value="/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  void deleteConfRemindersReminderId(@PathVariable("reminderId") String reminderId);

  /**
   * Retrieve a list of Reminders
   * This Web service returns a list of Reminders.  
   * @param type Type for filtering. Optional. Valid values are from the ReminderTypes code table (optional, default to &quot;&quot;)
   * @param status Status for filtering. Optional. Valid values are from the ReminderStatuses code table (optional, default to &quot;&quot;)
   * @param from From this Date (YYYY-MM-DD). Optional. Defaults to today. (optional, default to &quot;&quot;)
   * @param to To this Date (YYYY-MM-DD). Optional. Defaults to the From Date. (optional, default to &quot;&quot;)
   * @param orderBy Order by parameter. Optional. Valid values are type, status, reminder_date. (optional, default to &quot;type&quot;)
   * @param direction Direction parameter. Optional. Valid values are asc, desc. (optional, default to &quot;asc&quot;)
   * @param entityId Entity ID parameter. Optional. Valid value is an entity id. (optional, default to &quot;&quot;)
   * @param entityType Entity Type parameter. Optional. Valid value is BIB_MMS. (optional, default to &quot;BIB_MMS&quot;)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfReminders(@RequestParam("type") String type,
                          @RequestParam("status") String status,
                          @RequestParam("from") String from,
                          @RequestParam("to") String to,
                          @RequestParam("order_by") String orderBy,
                          @RequestParam("direction") String direction,
                          @RequestParam("entity_id") String entityId,
                          @RequestParam("entity_type") String entityType,
                          @RequestParam("limit") Integer limit,
                          @RequestParam("offset") Integer offset);

  /**
   * Retrieve a Reminder
   * This Web service returns a Reminder.  
   * @param reminderId Reminder ID. Required. (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.GET,
          value="/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object getConfRemindersReminderId(@PathVariable("reminderId") String reminderId);

  /**
   * Create a Reminder
   * This Web service creates a Reminder.  
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.POST,
          value="",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object postConfReminders(@RequestBody Object body);

  /**
   * Update a Reminder
   * This Web service updates a Reminder.  
   * @param reminderId Reminder ID. Required. (required)
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestMapping(method=RequestMethod.PUT,
          value="/{reminderId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE)
  Object putConfRemindersReminderId(@PathVariable("reminderId") String reminderId,
                                    @RequestBody Object body);
}

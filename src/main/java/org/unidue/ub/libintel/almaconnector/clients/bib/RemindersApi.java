package org.unidue.ub.libintel.almaconnector.clients.bib;

import feign.*;


public interface RemindersApi {


  /**
   * Delete a Reminder for BIB
   * This Web service deletes a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   */
  @RequestLine("DELETE /almaws/v1/bibs/{mmsId}/reminders/{reminderId}")
  @Headers({
    "Accept: application/json",
  })
  void deleteAlmawsV1BibsMmsIdRemindersReminderId(@Param("mms_id") String mmsId, @Param("reminder_id") String reminderId);

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
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/reminders?type={type}&status={status}&from={from}&to={to}&order_by={orderBy}&direction={direction}&limit={limit}&offset={offset}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdReminders(@Param("mms_id") String mmsId, @Param("type") String type, @Param("status") String status, @Param("from") String from, @Param("to") String to, @Param("order_by") String orderBy, @Param("direction") String direction, @Param("limit") Integer limit, @Param("offset") Integer offset);


  /**
   * Retrieve a Reminder for BIB
   * This Web service returns a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/bibs/{mmsId}/reminders/{reminderId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1BibsMmsIdRemindersReminderId(@Param("mms_id") String mmsId, @Param("reminder_id") String reminderId);

  /**
   * Creates a Reminder for BIB
   * This Web service creates a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;POST) (required)
   * @return Object
   */
  @RequestLine("POST /almaws/v1/bibs/{mmsId}/reminders")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object postAlmawsV1BibsMmsIdReminders(@Param("mms_id") String mmsId, Object body);

  /**
   * Update a Reminder for BIB
   * This Web service updates a Reminder for a BIB Entity.  
   * @param mmsId MMS ID. Required. (required)
   * @param reminderId Reminder ID. Required. (required)
   * @param body This method takes a reminder object. See [here](/alma/apis/docs/xsd/rest_reminder.xsd?tags&#x3D;PUT) (required)
   * @return Object
   */
  @RequestLine("PUT /almaws/v1/bibs/{mmsId}/reminders/{reminderId}")
  @Headers({
    "Content-Type: application/json",
    "Accept: application/json",
  })
  Object putAlmawsV1BibsMmsIdRemindersReminderId(@Param("mms_id") String mmsId, @Param("reminder_id") String reminderId, Object body);
}

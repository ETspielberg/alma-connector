package org.unidue.ub.libintel.almaconnector.clients.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.alma.shared.conf.Job;

import java.util.List;
import feign.*;
import org.unidue.ub.alma.shared.conf.JobInstance;
import org.unidue.ub.alma.shared.conf.JobInstances;

@FeignClient(name = "jobs", url = "/almaws/v1/conf/jobs", configuration = JobsFeignConfiguration.class)
@Service
public interface AlmaJobsApiClient {


  /**
   * Retrieve Jobs
   * This Web service returns a list of jobs that can be submitted.  The Jobs API supports 2 job types:  Manual jobs - Perform actions on a pre-defined set of records. Available in the Run a job list in the Alma UI.  Scheduled jobs  - Jobs that might be running periodically. In the Alma UI, it is possible to see these jobs (if they have a defined schedule) in the Scheduled tab of the Monitor Jobs page.  See [Working with the Alma Jobs API](https://developers.exlibrisgroup.com/blog/Working-with-the-Alma-Jobs-API) for more details.
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param category For filtering jobs by their category. Optional. For manual, possible values are from HPaTaskChain.type code table. For scheduled and other, possible values are from the systemJobsGroups code table. By default, all jobs will be retrieved. (optional, default to &quot;&quot;)
   * @param type For filtering jobs by their type. Optional. Possible values are MANUAL/SCHEDULED/OTHER. If no type is given, all types of jobs will be retrieved. (optional, default to &quot;&quot;)
   * @param profileId For filtering jobs by their profile ID. Optional. Relevant only for scheduled jobs. (optional, default to &quot;&quot;)
   * @return List<Job>
   */
  @RequestMapping(method = RequestMethod.GET, value = "/?limit={limit}&offset={offset}&category={category}&type={type}&profile_id={profile_id}")
  List<Job> getAlmawsV1ConfJobs(@RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset, @RequestParam("category") String category, @RequestParam("type") String type, @RequestParam("profile_id") String profileId);

  /**
   * Retrieve Job Details
   * This Web service returns job details for a given job id.
   * @param jobId Unique id of the job. Mandatory. (required)
   * @return Job
   */
  @RequestMapping(method = RequestMethod.GET, value = "/almaws/v1/conf/jobs/{job_id}")
  Job getAlmawsV1ConfJobsJobId(@RequestParam("job_id") String jobId);

  /**
   * Retrieve Job Instances
   * This Web service returns all the job instances (runs) for a given job id.
   * @param jobId Unique id of the job. Mandatory. (required)
   * @param submitDateFrom Retrieve instances from this Date (YYYY-MM-DD). Optional. (required)
   * @param submitDateTo Retrieve instances until this Date, included (YYYY-MM-DD). Optional. (required)
   * @param status Only instances with the specified status will be retrieved. Optional. (required)
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @return JobInstances
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{job_id}/instances?limit={limit}&offset={offset}&submit_date_from={submit_date_from}&submit_date_to={submit_date_to}&status={status}")
  JobInstances getAlmawsV1ConfJobsJobIdInstances(@RequestParam("job_id") String jobId, @RequestParam("submit_date_from") String submitDateFrom, @RequestParam("submit_date_to") String submitDateTo, @RequestParam("status") String status, @RequestParam("limit") Integer limit, @RequestParam("offset") Integer offset);
  /**
   * Retrieve Job Instance Details
   * This Web service returns a job instance for given job id and instance id.
   * @param jobId Unique id of the job. Mandatory. (required)
   * @param instanceId Unique id of the specific job instance. Mandatory. (required)
   * @return JobInstance
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{jobId}/instances/{instance_id}")
  JobInstance getAlmawsV1ConfJobsJobIdInstancesInstanceId(@RequestParam("job_id") String jobId, @RequestParam("instance_id") String instanceId);

  /**
   * Submit a manual or scheduled job
   * This Web service submits a job according to a given job id.  The Jobs API supports 2 job types:  Manual jobs - Perform actions on a pre-defined set of records. Available in the Run a job list in the Alma UI.  Scheduled jobs  - Jobs that might be running periodically. In the Alma UI, it is possible to see these jobs (if they have a defined schedule) in the Scheduled tab of the Monitor Jobs page.  In order to submit a manual job by API, the relevant parameters should be supplied in the input.  See [Working with the Alma Jobs API](https://developers.exlibrisgroup.com/blog/Working-with-the-Alma-Jobs-API) for more details.   To maintain optimum performance the following threshold will be used: The job will run only if:    a. not more than 3 jobs initiated by the API are running currently.    b. not more of 5 of the specific job were started in the previous hour.   The submission of scheduled jobs is supported for: ERP (Export Invoices, Import confirmation), Metadata Import, Remote Storage (Inventory Update, Send Requests to Remote Storage), Export/Import to Bursar, General Publishing (&#39;republish&#39; action is currently not supported) and Student Information System (Import, Synchronize and Export).
   * @param jobId Unique id of the job. Mandatory. (required)
   * @param op The operation to perform on the job. Currently op&#x3D;run is supported for manual and scheduled jobs. (required)
   * @param body This method takes a Job object. See [here](/alma/apis/docs/xsd/rest_job.xsd?tags&#x3D;POST) (required)
   * @return Job
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{job_id}?op={op}")
  Job postAlmawsV1ConfJobsJobId( @RequestBody Job body, @RequestParam("job_id") String jobId, @RequestParam("op") String op);
}

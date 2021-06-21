package org.unidue.ub.libintel.almaconnector.clients.alma.conf;

import feign.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;

@FeignClient(name = "depositProfiles", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/conf/deposit-profiles", configuration = AlmaFeignConfiguration.class)
@Service
public interface DepositProfilesApi {


  /**
   * Retrieve Deposit Profiles
   * This web service returns a list of Deposit Profiles.
   * @param limit Limits the number of results. Optional. Valid values are 0-100. Default value: 10. (optional)
   * @param offset Offset of the results returned. Optional. Default value: 0, which means that the first results will be returned. (optional)
   * @param userGroup An option to filter by user group. Optional. Default value: Empty String which means that all the results will be returned. (optional, default to &quot;&quot;)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/deposit-profiles?limit={limit}&offset={offset}&user_group={userGroup}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfDepositProfiles(@Param("limit") Integer limit, @Param("offset") Integer offset, @Param("user_group") String userGroup);


  /**
   * Retrieve Deposit Profile
   * This web service returns a specific Deposit Profile.
   * @param depositProfileId The Deposit Profile ID. (required)
   * @return Object
   */
  @RequestLine("GET /almaws/v1/conf/deposit-profiles/{depositProfileId}")
  @Headers({
    "Accept: application/json",
  })
  Object getAlmawsV1ConfDepositProfilesDepositProfileId(@Param("deposit_profile_id") String depositProfileId);
}

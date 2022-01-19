package org.unidue.ub.libintel.almaconnector.clients.alma.electronic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.alma.shared.bibs.*;
import org.unidue.ub.libintel.almaconnector.clients.alma.AlmaFeignConfiguration;
import org.springframework.http.MediaType;

@FeignClient(name = "electronic", url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/electronic", configuration = AlmaFeignConfiguration.class)
@Service
public interface AlmaElectronicApiClient {


  /**
   * Delete Bib Record
   * This web service deletes a Bib Record.  For more information regarding the various options supported for this API see [here](https://developers.exlibrisgroup.com/blog/cataloging-APIs-enhancements).
   * @param collectionId The ID of the electronic collection. (required)
   * @param serviceId The ID of the electronic service. (required)
   * @param portfolio The portfolio to be added to the collection.
   */
  @RequestMapping(method= RequestMethod.POST, value="/e-collections/{collectionId}/e-services/{serviceId}/portfolios", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  Portfolio createElectronicPortfolio(@PathVariable("collectionId") String collectionId,
                                      @PathVariable("serviceId") String serviceId,
                                      @RequestBody Portfolio portfolio);
}

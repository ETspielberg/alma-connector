package org.unidue.ub.libintel.almaconnector.clients.getter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsPrintManifestation;

import java.util.List;

/**
 * Uses the address API of the register application in order to obtain address data for the students queries by the ZIM ID
 */
@FeignClient(name = "getter", configuration = GetterConfiguration.class)
@Service
public interface GetterClient {

    @RequestMapping(method= RequestMethod.GET, value="/es/manifestations/{mode}/{identifier}")
    List<EsPrintManifestation> getManifestations(@PathVariable("mode") String mode, @PathVariable("identifier") String identifier);

    @RequestMapping(method= RequestMethod.POST, value="/es/manifestation")
    EsPrintManifestation saveManifestation(@RequestBody EsPrintManifestation manifestation);
}


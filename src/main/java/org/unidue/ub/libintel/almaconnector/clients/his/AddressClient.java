package org.unidue.ub.libintel.almaconnector.clients.his;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.almaconnector.model.his.Address;

/**
 * Uses the address API of the register application in order to obtain address data for the students queries by the ZIM ID
 */
@FeignClient(name = "his", url = "https://register.ub.uni-due.de/api", configuration = HisConfiguration.class)
@Service
public interface AddressClient {

    /**
     * retrieves an <class>Address</class> object from the university students address server
     * @param zimkennung the zim id of the the student
     * @return an address object holding the address of the student
     */
    @RequestMapping(method= RequestMethod.GET, value="/address/{zimkennung}")
    Address getAddressForZimKennung(@PathVariable String zimkennung);

}

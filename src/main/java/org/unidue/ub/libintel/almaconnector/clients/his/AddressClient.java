package org.unidue.ub.libintel.almaconnector.clients.his;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.unidue.ub.libintel.almaconnector.model.his.Address;

@FeignClient(name = "his", url = "https://register.ub.uni-due.de/api", configuration = HisConfiguration.class)
@Service
public interface AddressClient {

    @RequestMapping(method= RequestMethod.GET, value="/address/{zimkennung}")
    Address getAddressForZimKennung(@PathVariable String zimkennung);

}

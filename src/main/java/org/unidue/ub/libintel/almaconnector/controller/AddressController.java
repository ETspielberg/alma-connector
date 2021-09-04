package org.unidue.ub.libintel.almaconnector.controller;

import feign.FeignException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.his.AddressClient;
import org.unidue.ub.libintel.almaconnector.model.his.Address;

/**
 * provides endpoints to retrieve students addresses from the HisInOne-System
 */
@Controller
@RequestMapping("/his")
public class AddressController {

    private final AddressClient addressClient;

    /**
     * constructor based autowiring to the address client
     * @param addressClient the address client bean
     */
    AddressController(AddressClient addressClient) {
        this.addressClient = addressClient;
    }

    /**
     * retrieves the start page
     * @return the start page
     */
    @GetMapping("/start")
    public String getStartPage() {
        return "his/start";
    }

    /**
     * the page showing the retrieved address
     * @param zimkennung the id for which the address is to be retrieved
     * @param model the model object for the page
     * @return the address page
     */
    @GetMapping("/address")
    public String getAddressPage(@RequestParam String zimkennung, Model model) {
        Address address;
        try {
            address = addressClient.getAddressForZimKennung(zimkennung);
        } catch (FeignException fe) {
            address = null;
        }
        model.addAttribute("address", address);
        return "his/address";
    }
}

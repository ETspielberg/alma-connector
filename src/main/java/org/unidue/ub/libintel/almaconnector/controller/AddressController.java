package org.unidue.ub.libintel.almaconnector.controller;

import feign.FeignException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.almaconnector.clients.his.AddressClient;
import org.unidue.ub.libintel.almaconnector.model.his.Address;

@Controller
@RequestMapping("/his")
public class AddressController {

    private final AddressClient addressClient;

    AddressController(AddressClient addressClient) {
        this.addressClient = addressClient;
    }

    @GetMapping("/start")
    public String getStartPage() {
        return "his/start";
    }

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

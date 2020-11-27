package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.almaconnector.service.BubiService;

@Controller
@RequestMapping("/bubi")
public class BubiController {

    private final BubiService bubiService;

    BubiController(BubiService bubiService) {
        this.bubiService = bubiService;
    }

    @GetMapping("/start")
    public String getStartPage() {
        return "bubiStart";
    }

    @GetMapping("/coredata/list")
    public String getCoredataListPage(Model model) {
        model.addAttribute("coreData", this.bubiService.getAllCoreData());
        return "coredataList";
    }
}

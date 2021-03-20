package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bubi")
public class BubiLetterController {

    @GetMapping("/letter")
    public String getStartPage(Model model, String bubi, String order) {


        return "bubi/order";
    }
}

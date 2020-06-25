package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * the simple page controllers
 */
@Controller
public class StartController {

    @GetMapping("/start")
    public String getStartPage() {
        return "start";
    }
}

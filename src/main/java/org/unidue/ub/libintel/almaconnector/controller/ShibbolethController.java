package org.unidue.ub.libintel.almaconnector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.unidue.ub.libintel.almaconnector.model.jobs.ShibbolethData;
import org.unidue.ub.libintel.almaconnector.service.ShibbolethDataService;

@Controller
@RequestMapping("/shibboleth")
public class ShibbolethController {

    private final ShibbolethDataService shibbolethDataService;

    private final Logger log = LoggerFactory.getLogger(ShibbolethController.class);

    public ShibbolethController(ShibbolethDataService shibbolethDataService) {
        this.shibbolethDataService = shibbolethDataService;
    }

    @GetMapping("edit")
    public String editPlatform(String platform, Model model) {
        model.addAttribute("platform", shibbolethDataService.getDataForPlatform(platform));
        return "shibboleth/edit";
    }


    @GetMapping("/start")
    public String getStartPage(Model model) {
        model.addAttribute("shibbolethData", this.shibbolethDataService.getAllShibbolethData());
        return "shibboleth/start";
    }

    @GetMapping("/confirmDelete")
    public String confirmDeletion(String platform, Model model) {
        model.addAttribute("platformToDelete", platform);
        return "shibboleth/delete";
    }

    @PostMapping("/delete")
    public String deletePlatform(WebRequest request, Model model) {

        String platform = request.getParameter("platformToDelete");
        log.info(String.format("deleting shibboleth data for platform %s", platform));
        this.shibbolethDataService.delete(platform);
        return getStartPage(model);
    }

    @PostMapping("/save")
    public String saveShibbolethData(@ModelAttribute ShibbolethData platform, Model model) {
        this.shibbolethDataService.save(platform);
        return getStartPage(model);
    }
}

package org.unidue.ub.libintel.almaconnector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.alma.shared.acq.Account;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrder;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaVendorService;
import org.unidue.ub.libintel.almaconnector.service.bubi.BubiOrderService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bubi")
public class BubiLetterController {

    private final BubiOrderService bubiOrderService;

    private final AlmaVendorService almaVendorService;

    BubiLetterController(BubiOrderService bubiOrderService,
                         AlmaVendorService almaVendorService) {
        this.bubiOrderService = bubiOrderService;
        this.almaVendorService = almaVendorService;
    }

    @GetMapping("/order/letter/{bubiOrderId}")
    public String getStartPage(@PathVariable String bubiOrderId, Model model) {
        BubiOrder bubiOrder = this.bubiOrderService.getBubiOrder(bubiOrderId);
        Map<String, List<BubiOrderLine>> typedOrderlines = bubiOrder.returnOrderLinesByMediatype();
        Vendor vendor = this.almaVendorService.getVendorAccount(bubiOrder.getVendorId());
        for (Account account: vendor.getAccount())
            if (account.getCode().equals(bubiOrder.getVendorAccount()))
                model.addAttribute("account", account);
        long standard = 0L;
        for (BubiOrderLine bubiOrderLine : typedOrderlines.get("book")) {
            if (bubiOrderLine.getStandard())
                standard++;
        }
        model.addAttribute("typedOrderlines", typedOrderlines);
        model.addAttribute("bubi", vendor);
        model.addAttribute("order", bubiOrder);
        return "bubi/order";
    }
}

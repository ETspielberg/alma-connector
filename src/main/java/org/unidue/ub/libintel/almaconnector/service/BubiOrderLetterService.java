package org.unidue.ub.libintel.almaconnector.service;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.unidue.ub.alma.shared.acq.Address;
import org.unidue.ub.alma.shared.acq.Vendor;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrder;

public class BubiOrderLetterService {

    private final TemplateEngine templateEngine;

    private final VendorService vendorService;

    public BubiOrderLetterService(TemplateEngine templateEngine,
                                  VendorService vendorService) {
        this.templateEngine = templateEngine;
        this.vendorService = vendorService;
    }

    public String buildLetter(String vendorId, BubiOrder bubiOrder) {
        Vendor vendor = this.vendorService.getVendorAccount(vendorId);
        Context context = new Context();
        context.setVariable("bubiName", vendor.getName());
        for (Address address: vendor.getContactInfo().getAddress())
            if (address.getPreferred()) {
                context.setVariable("bubiFirstLine", address.getLine1());
                context.setVariable("bubiSecondLine", address.getLine2());
                context.setVariable("bubiPostalCode", address.getPostalCode());
                context.setVariable("bubiSecondCity", address.getCity());
            }
        context.setVariable("order", bubiOrder);
        return templateEngine.process("bubi/order", context);
    }
}

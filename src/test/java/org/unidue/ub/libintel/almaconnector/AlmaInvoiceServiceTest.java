package org.unidue.ub.libintel.almaconnector;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.unidue.ub.libintel.almaconnector.service.alma.AlmaInvoiceService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlmaInvoiceServiceTest {

    @Autowired
    AlmaInvoiceService almaInvoiceService;

    @Test
    public void loadAllActiveInvoices() {
        String owner = "E0001";
        this.almaInvoiceService.getOpenInvoices(owner);
    }
}

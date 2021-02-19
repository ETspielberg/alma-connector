package org.unidue.ub.libintel.almaconnector;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.unidue.ub.libintel.almaconnector.service.AlmaInvoiceServices;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlmaInvoiceServiceTest {

    @Autowired
    AlmaInvoiceServices almaInvoiceServices;

    @Test
    public void loadAllActiveInvoices() {
        String owner = "E0001";
        this.almaInvoiceServices.getOpenInvoices(owner);
    }
}

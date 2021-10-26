package org.unidue.ub.libintel.almaconnector;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.unidue.ub.alma.shared.acq.Invoice;
import org.unidue.ub.libintel.almaconnector.model.sap.SapAccountData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.unidue.ub.libintel.almaconnector.service.SapService.convertFundCodeToSapAccountData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UtilsTests {

    SimpleDateFormat testDateformatter=new SimpleDateFormat("dd-MM-yyyy");

    private final Logger log = LoggerFactory.getLogger(UtilsTests.class);

    private final List<String> ledgers = Arrays.asList("0001-0000-2020",
            "0012-0200-2020",
            "5028-0000-2020",
            "5027-0700-2020",
            "1004-1400-0000-2020",
            "1036-1000-0500-2020",
            "55510-0-0000-2020",
            "55510-0-0210-2020",
            "55510-5-0000-NB-2020",
            "55510-5-0400-AUSGLEICH-2020",
            "S-201000000-1000-0000-2020",
            "S-55300000030002-2020-0200-2020",
            "S-55510000030006-2020-68900100-2020",
            "0021-0000-VW-2020",
            "0014-0400-PA-2020",
            "5002-0000-NL-2020",
            "5016-0800-2020",
            "1005-1008-0000-2020",
            "1049-1400-0200-2020",
            "55510-0-0000-ERSATZ-2020",
            "55510-0-0400-NW-2020",
            "55510-5-0800-NB-2020",
            "55510-5-0600-INGWI-2020",
            "S-40201020250024-2140-0000-2020",
            "S-402010203-1000-0200-2020",
            "S-55510000090053-1000-68900100-2020"
    );

    private final List<String> checkStrings = Arrays.asList("681000001000P55510000090001",
            "681002001000P55510000090012",
            "681000001415P55510000095028",
            "681007001415P55510000095027",
            "681000001400P55510000091004",
            "681005001000P55510000091036",
            "681000001000K555100000",
            "681002101000K555100000",
            "681000001400K555100000",
            "681004001400K555100000",
            "681000001000K201000000",
            "681002002020P55300000030002",
            "689001002020P55510000030006",
            "681000001000P55510000090021",
            "681004001000P55510000090014",
            "681000001415P55510000095002",
            "681008001415P55510000095016",
            "681000001008P55510000091005",
            "681002001400P55510000091049",
            "681000001000K555100000",
            "681004001000K555100000",
            "681008001400K555100000",
            "681006001400K555100000",
            "681000002140P40201020250024",
            "681002001000K402010203",
            "689001001000P55510000090053"
    );



    private final List<String> fondsResults = Arrays.asList("1000",
            "1000",
            "1400",
            "1400",
            "1400",
            "1000",
            "1000",
            "1000",
            "1400",
            "1400",
            "1000",
            "2020",
            "2020",
            "1000",
            "1000",
            "1400",
            "1400",
            "1008",
            "1400",
            "1000",
            "1000",
            "1400",
            "1400",
            "2140",
            "1000",
            "1000"
    );

    private final List<String> pspElementResults = Arrays.asList("55510000090001",
            "55510000090012",
            "55510000095028",
            "55510000095027",
            "55510000091004",
            "55510000091036",
            "",
            "",
            "",
            "",
            "",
            "55300000030002",
            "55510000030006",
            "55510000090021",
            "55510000090014",
            "55510000095002",
            "55510000095016",
            "55510000091005",
            "55510000091049",
            "",
            "",
            "",
            "",
            "40201020250024",
            "",
            "55510000090053"

    );

    private final List<String> ledgerAccountResults = Arrays.asList("68100000",
            "68100200",
            "68100000",
            "68100700",
            "68100000",
            "68100500",
            "68100000",
            "68100210",
            "68100000",
            "68100400",
            "68100000",
            "68100200",
            "68900100",
            "68100000",
            "68100400",
            "68100000",
            "68100800",
            "68100000",
            "68100200",
            "68100000",
            "68100400",
            "68100800",
            "68100600",
            "68100000",
            "68100200",
            "68900100"


    );

    private final List<String> costCentreResults = Arrays.asList("",
            "",
            "",
            "",
            "",
            "",
            "555100000",
            "555100000",
            "555100000",
            "555100000",
            "201000000",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "555100000",
            "555100000",
            "555100000",
            "555100000",
            "",
            "402010203",
            "");


    private Invoice testInvoice;

    public UtilsTests() throws ParseException {
    }

    @Before
    public void setupTestInvoice() {
        try {
            Date testInvoiceDate = testDateformatter.parse("01-05-2020");
            testInvoice = new Invoice().invoiceDate(testInvoiceDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

    }

    @Test
    public void testSapAccountDataGeneration() {
        for (int i = 0; i < ledgers.size(); i++) {
            log.debug(this.ledgers.get(i));
            SapAccountData sapData = convertFundCodeToSapAccountData(this.ledgers.get(i));
            log.debug(i + ": Kostenstelle " + sapData.getCostCentre() + " == " + this.costCentreResults.get(i));
            assertEquals(this.costCentreResults.get(i), sapData.getCostCentre());
            log.debug(i + ": Fonds " + sapData.getFonds() + " == " + this.fondsResults.get(i));
            assertEquals(this.fondsResults.get(i), sapData.getFonds());
            log.debug(i + ": Sachkonto " + sapData.getLedgerAccount() + " == " + this.ledgerAccountResults.get(i));
            assertEquals(this.ledgerAccountResults.get(i), sapData.getLedgerAccount());
            log.debug(i + ": PSP-Element " + sapData.getPspElement() + " == " + this.pspElementResults.get(i));
            assertEquals(this.pspElementResults.get(i), sapData.getPspElement());
            log.debug(i + ": Check-String " + sapData.getImportCheckString() + " == " + this.checkStrings.get(i));
        }
    }
}

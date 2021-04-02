package org.unidue.ub.libintel.almaconnector.service.alma;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.acq.*;
import org.unidue.ub.libintel.almaconnector.clients.acquisition.AlmaPoLinesApiClient;
import org.unidue.ub.libintel.almaconnector.model.bubi.BubiOrderLine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlmaPoLineService {

    private final AlmaPoLinesApiClient almaPoLinesApiClient;

    /**
     * constructor based autowiring to the Feign client
     * @param almaPoLinesApiClient the Feign client for the Alma po line API
     */
    AlmaPoLineService(AlmaPoLinesApiClient almaPoLinesApiClient) {
        this.almaPoLinesApiClient = almaPoLinesApiClient;
    }

    /**
     * retrieves the active po-lines.
     * @return a list of po-lines
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" })
    public List<PoLine> getOpenPoLines() {
        // initialize parameters
        int batchSize = 100;
        int offset = 0;

        // retrieve first list of po-lines.
        PoLines poLines = this.almaPoLinesApiClient.getPoLines("application/json", "", "ACTIVE", batchSize, offset, "", "", "", "", "", "", "");
        List<PoLine> poLineList = new ArrayList<>(poLines.getPoLine());

        // as long as not all data are being collected, collect further
        while (poLineList.size() < poLines.getTotalRecordCount()) {
            offset += batchSize;
            poLines = this.almaPoLinesApiClient.getPoLines("application/json", "", "ACTIVE", batchSize, offset, "", "", "", "", "", "", "");
            poLineList.addAll(poLines.getPoLine());
        }
        return poLineList;
    }

    /**
     * retrieves the active po-lines.
     * @return a list of po-lines
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine savePoLine(PoLine poLine) {
        return this.almaPoLinesApiClient.postAcqPoLines(poLine, "application/json", "");
    }

    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine updatePoLine(PoLine poLine) {
        return this.almaPoLinesApiClient.putPoLinesPoLineId(poLine, "application/json", poLine.getNumber(),"" );
    }

    @Secured({ "ROLE_SYSTEM", "ROLE_ALMA" , "ROLE_ALMA_Physical_Inventory_Operator"})
    public PoLine getPoLine(String poLineId) {
        return this.almaPoLinesApiClient.getPoLinesPoLineId("application/json", poLineId);
    }

    public boolean closePoLine(PoLine poLine) {
        poLine.setStatus(new PoLineStatus().value("CLOSED"));
        poLine = this.almaPoLinesApiClient.putPoLinesPoLineId(poLine, "application/json", poLine.getNumber(), "false");
        return "CLOSED".equals(poLine.getStatus().getValue());
    }

    /**
     * creates an Alma PO Line form the bubi order line
     *
     * @param bubiOrderLine the bubi order line from which the Alma PO Line is created
     * @return an Alma PoLine object
     */
    public PoLine buildPoLine(BubiOrderLine bubiOrderLine, LocalDate expectedOn) {
        PoLineOwner poLineOwner;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // set the owner depending on the collection
        if (bubiOrderLine.getCollection().startsWith("D"))
            poLineOwner = new PoLineOwner().value("D0001");
        else if (bubiOrderLine.getCollection().startsWith("E5"))
            poLineOwner = new PoLineOwner().value("E0023");
        else
            poLineOwner = new PoLineOwner().value("E0001");

        // creates the amount and fund information
        Amount amount = new Amount().sum(String.valueOf(bubiOrderLine.getPrice()))
                .currency(new AmountCurrency().value("EUR"));
        FundDistributionPoLine fundDistribution = new FundDistributionPoLine()
                .fundCode(new FundDistributionFundCode().value(bubiOrderLine.getFund()))
                .amount(amount);
        List<FundDistributionPoLine> fundList = new ArrayList<>();
        fundList.add(fundDistribution);

        // creates the resource metadata
        ResourceMetadata resourceMetadata = new ResourceMetadata()
                .mmsId(new ResourceMetadataMmsId().value(bubiOrderLine.getAlmaMmsId()));
                //.title(bubiOrderLine.getTitle());

        // sets the status to a auto packaging
        PoLineStatus status = new PoLineStatus().value("AUTO_PACKAGING").desc("Auto Packaging");
        Note note = new Note().noteText(String.format("Zurückerwartet am %s", formatter.format(expectedOn)));
        return new PoLine()
                .reclaimInterval("21")
                .vendorReferenceNumber(String.format("%s - %S:%s)", bubiOrderLine.getFund(),
                        bubiOrderLine.getCollection(),
                        bubiOrderLine.getShelfmark()))
                .sourceType(new PoLineSourceType().value("MANUALENTRY"))
                .type(new PoLineType().value("OTHER_SERVICES_OT"))
                .status(status)
                .price(amount)
                .baseStatus(PoLine.BaseStatusEnum.ACTIVE)
                .owner(poLineOwner)
                .resourceMetadata(resourceMetadata)
                .vendor(new PoLineVendor().value(bubiOrderLine.getVendorId()))
                .vendorAccount(bubiOrderLine.getVendorAccount())
                .fundDistribution(fundList)
                .addNoteItem(note);
    }

    public void updatePoLineByBubiOrderLine(BubiOrderLine bubiOrderLine) {
        PoLine poLine = this.almaPoLinesApiClient.getPoLinesPoLineId("application/json", bubiOrderLine.getAlmaPoLineId());
        Amount amount = new Amount().sum(String.valueOf(bubiOrderLine.getPrice()))
                .currency(new AmountCurrency().value("EUR"));
        poLine.getFundDistribution().get(0).setAmount(amount);
        this.almaPoLinesApiClient.putPoLinesPoLineId(poLine, "application/json", bubiOrderLine.getAlmaPoLineId(), "false");
    }
}

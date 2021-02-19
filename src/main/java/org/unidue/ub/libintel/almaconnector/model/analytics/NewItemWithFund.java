package org.unidue.ub.libintel.almaconnector.model.analytics;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "row")
public class NewItemWithFund {

    public final static String PATH = "/shared/Universität Duisburg-Essen 49HBZ_UDE/libintel/NewItemsWithFund";

    @JacksonXmlProperty(localName = "FundLedgerCode")
    private String fundLedgerCode;

    @JacksonXmlProperty(localName = "StatisticsNote1")
    private String statisticNote1;

    @JacksonXmlProperty(localName = "CreationDate")
    private String creationDate;

    @JacksonXmlProperty(localName = "MmsId")
    private String mmsId;

    @JacksonXmlProperty(localName = "ItemId")
    private String itemId;


    public String getFundLedgerCode() {
        return fundLedgerCode;
    }

    public void setFundLedgerCode(String fundLedgerCode) {
        this.fundLedgerCode = fundLedgerCode;
    }

    public String getStatisticNote1() {
        return statisticNote1;
    }

    public void setStatisticNote1(String statisticNote1) {
        this.statisticNote1 = statisticNote1;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getMmsId() {
        return mmsId;
    }

    public void setMmsId(String mmsId) {
        this.mmsId = mmsId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

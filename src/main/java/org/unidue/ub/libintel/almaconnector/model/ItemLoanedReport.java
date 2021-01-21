package org.unidue.ub.libintel.almaconnector.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "itemLoanedReport")
@JacksonXmlRootElement(localName = "itemLoanedReport")
public class ItemLoanedReport {

    @XmlElement(name = "itemLoaned")
    private List<ItemLoaned> itemLoaneds;

    public List<ItemLoaned> getItemLoanedReports() {
        return itemLoaneds;
    }

    public void setItemLoanedReports(List<ItemLoaned> itemLoaneds) {
        this.itemLoaneds = itemLoaneds;
    }
}

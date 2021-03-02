package org.unidue.ub.libintel.almaconnector.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "itemLoaned")
@JacksonXmlRootElement(localName = "itemLoaned")
public class ItemLoaned {

    @XmlElement(name = "loanDate")
    private Date loanDate;

    @XmlElement(name = "itemCallNumber")
    private String itemCallNumber;

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getItemCallNumber() {
        return itemCallNumber;
    }

    public void setItemCallNumber(String itemCallNumber) {
        this.itemCallNumber = itemCallNumber;
    }
}

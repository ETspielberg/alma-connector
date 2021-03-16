package org.unidue.ub.libintel.almaconnector.model.sap;

/**
 * Container object for all SAP account related data.
 */
public class SapAccountData {

    private String fonds = "";

    private String costCentre = "";

    private String pspElement = "";

    private String ledgerAccount = "";

    public SapAccountData() { }

    public SapAccountData withFonds(String fonds) {
        this.fonds = fonds;
        return this;
    }

    public SapAccountData withCostCentre(String costCentre) {
        this.costCentre = costCentre;
        return this;
    }

    public SapAccountData withPspElement(String pspElement) {
        this.pspElement = pspElement;
        return this;
    }

    public SapAccountData withLedgerAccount(String ledgerAccount) {
        this.ledgerAccount = ledgerAccount;
        return this;
    }

    /**
     * @return the check string used to check the correct SAP data
     */
    public String getImportCheckString() {
        if (this.ledgerAccount != "") {
            if (!this.pspElement.isEmpty())
                return this.ledgerAccount + this.fonds + "P" + this.pspElement;
            else
                return this.ledgerAccount + this.fonds + "K" + costCentre;
        } else {
            return "";
        }
    }

    public String getSapString() {
        if (!this.pspElement.isEmpty())
            return this.ledgerAccount  + this.fonds + "P" + this.pspElement;
        else
            return this.ledgerAccount + this.fonds +  "K" + costCentre;
    }

    public String getFonds() {
        return fonds;
    }

    public void setFonds(String fonds) {
        this.fonds = fonds;
    }

    public String getCostCentre() {
        return costCentre;
    }

    public void setCostCentre(String costCentre) {
        this.costCentre = costCentre;
    }

    public String getPspElement() {
        return pspElement;
    }

    public void setPspElement(String pspElement) {
        this.pspElement = pspElement;
    }

    public String getLedgerAccount() {
        return ledgerAccount;
    }

    public void setLedgerAccount(String ledgerAccount) {
        this.ledgerAccount = ledgerAccount;
    }
}

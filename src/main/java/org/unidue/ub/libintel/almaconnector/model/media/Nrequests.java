/**
 *
 */
package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Plain old java object holding the entrie of the requests hitlist.
 *
 * @author Eike Spielberg
 * @version 1
 */
public class Nrequests implements Cloneable {

    private String mab;

    private String identifier;

    private String titleId;

    private String isbn;

    private String shelfmark;

    private double ratio;

    public int NRequests;

    public int NItems;

    public int NLoans;

    public int NLendable;

    public long totalDuration;

    public String status;

    @Transient
    private List<String> forAlertControls;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date date;

    /**
     * general constructor and initialization
     */
    public Nrequests() {
        titleId = "";
        ratio = 0.0;
        shelfmark = "";
        NRequests = 0;
        NItems = 0;
        NLoans = 0;
        NLendable = 0;
        mab = "";
        totalDuration = 0L;
        identifier = this.titleId + System.currentTimeMillis();
        date = new Date();
        status = "NEW";
        isbn = "";
    }

    public Nrequests(String titleId, String shelfmark, double ratio, int NItems, int NLendable, int NRequests, int NLoans) {
        this.titleId = titleId;
        this.shelfmark = shelfmark;
        this.NItems = NItems;
        this.NLendable = NLendable;
        this.NLoans = NLoans;
        this.NRequests = NRequests;
        this.ratio = ratio;
        this.isbn = "";
        date = new Date();
        identifier = this.titleId + System.currentTimeMillis();
        mab = "";
        totalDuration = 1L;
        status = "NEW";
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    /**
     * returns the shelfmark
     *
     * @return the callNo
     */
    public String getCallNo() {
        return shelfmark;
    }

    /**
     * returns the duration of requests
     *
     * @return the duration
     */
    public long geTotalDuration() {
        return totalDuration;
    }

    /**
     * sets the duration
     *
     * @param totalDuration the duration to set
     */
    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    /**
     * sets the shelfmark
     *
     * @param callNo the callNo to set
     */
    public void setCallNo(String callNo) {
        this.shelfmark = callNo;
    }

    /**
     * returns the bibliographic data
     *
     * @return the mab
     */
    public String getMab() {
        return mab;
    }

    /**
     * sets the bibliographic data
     *
     * @param mab the mab to set
     */
    public void setMab(String mab) {
        this.mab = mab;
    }

    /**
     * returns the document number for the manifestation
     *
     * @return the titleId
     */
    public String getTitleId() {
        return titleId;
    }

    /**
     * sets the document number for this manifestation
     *
     * @param titleId the titleId to set
     */
    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    /**
     * returns the ratio (number of requested items / number of lendable items)
     *
     * @return the ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * sets the ratio (number of requested items / number of lendable items)
     *
     * @param ratio the ratio to set
     */
    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    /**
     * returns the number of requests
     *
     * @return the nRequests
     */
    public int getNRequests() {
        return NRequests;
    }

    /**
     * sets the number of requests
     *
     * @param nRequests the nRequests to set
     */
    public void setNRequests(int nRequests) {
        NRequests = nRequests;
    }

    /**
     * returns the number of items
     *
     * @return the nItems
     */
    public int getNItems() {
        return NItems;
    }

    /**
     * sets the number of items
     *
     * @param nItems the nItems to set
     */
    public void setNItems(int nItems) {
        NItems = nItems;
    }

    /**
     * returns the number of loans
     *
     * @return the nLoans
     */
    public int getNLoans() {
        return NLoans;
    }

    /**
     * sets the number of loans
     *
     * @param nLoans the nLoans to set
     */
    public void setNLoans(int nLoans) {
        NLoans = nLoans;
    }

    /**
     * returns the number of lendable items
     *
     * @return the nLendable
     */
    public int getNLendable() {
        return NLendable;
    }

    /**
     * sets the number of lendable items
     *
     * @param nLendable the nLendable to set
     */
    public void setNLendable(int nLendable) {
        NLendable = nLendable;
    }

    /**
     * returns the timestamp
     *
     * @return the timestamp
     */
    public Date getDate() {
        return date;
    }

    /**
     * sets the number of date
     *
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * returns the alert control
     *
     * @return the alert control
     */
    public List<String> getForAlertControls() {
        return forAlertControls;
    }

    /**
     * sets the number of alert control
     *
     * @param forAlertControls the alert control to set
     */
    public void setForAlertControls(List<String> forAlertControls) {
        this.forAlertControls = forAlertControls;
    }


    public void addForAlertControls(String forAlertControl) {
        forAlertControls.add(forAlertControl);
    }

    public void updateRatio() {
        ratio = (double) NRequests / (double) NLendable;
    }


    public Nrequests clone() {
        Nrequests clone = new Nrequests();
        clone.setCallNo(shelfmark);
        clone.setTitleId(titleId);
        clone.setTotalDuration(totalDuration);
        clone.setMab(mab);
        clone.setNItems(NItems);
        clone.setNLendable(NLendable);
        clone.setNLoans(NLoans);
        clone.setNRequests(NRequests);
        clone.setForAlertControls(forAlertControls);
        clone.setIdentifier(identifier);
        clone.setDate(date);
        return clone;
    }
} 
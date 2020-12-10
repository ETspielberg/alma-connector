package org.unidue.ub.libintel.almaconnector.model.bubi;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.unidue.ub.alma.shared.acq.Vendor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="bubi_order")
public class BubiOrder {

    @Id
    @Column(name="bubi_order_id")
    private String bubiOrderId;

    @Column(name="alma_order_id")
    private String almaOrderId;

    @OneToMany(mappedBy = "bubiOrder")
    private List<BubiOrderLine> bubiOrderLines;

    @Column(name="bubi_status")
    private BubiStatus bubiStatus;

    @Column(name="comment")
    private String comment;

    @Column(name="alma_po_line_number")
    private String almaPoLineNumber;

    @Transient
    private Vendor bubiData;

    @Column(name="created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date created;

    @Column(name="last_change")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastChange;

    public List<BubiOrderLine> getBubiOrderLines() {
        return bubiOrderLines;
    }

    public void setBubiOrderLines(List<BubiOrderLine> bubiOrderLines) {
        this.bubiOrderLines = bubiOrderLines;
    }

    public BubiStatus getBubiStatus() {
        return bubiStatus;
    }

    public void setBubiStatus(BubiStatus bubiStatus) {
        this.bubiStatus = bubiStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAlmaPoLineNumber() {
        return almaPoLineNumber;
    }

    public void setAlmaPoLineNumber(String almaPoLineNumber) {
        this.almaPoLineNumber = almaPoLineNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }
}

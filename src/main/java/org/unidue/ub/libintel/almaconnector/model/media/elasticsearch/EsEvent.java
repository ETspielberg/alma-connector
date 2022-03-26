package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import org.unidue.ub.alma.shared.bibs.ItemLoan;
import org.unidue.ub.alma.shared.bibs.LoanStatus;
import org.unidue.ub.libintel.almaconnector.model.EventType;

import java.util.Date;

/**
 * Representation object of one event from one of the three groups loans (loan
 * and return) requests (request and hold) and stock (inventory and deletion).
 *
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
public class EsEvent implements Comparable<EsEvent> {

    private final static long dayInMillis = 86400000L;

    private Date beginDate;

    private Date endDate;

    private EventType type;

    private String borrowerStatus;

    private String eventId;

    public EsEvent(ItemLoan itemLoan) {
        this.type = EventType.LOAN;
        this.beginDate = new Date(itemLoan.getLoanDate().toInstant().toEpochMilli());
        this.eventId = itemLoan.getLoanId();
        if (itemLoan.getLoanStatus().equals(LoanStatus.COMPLETE))
            this.endDate = new Date(itemLoan.getReturnDate().toInstant().toEpochMilli());
    }


    /**
     * @param type the type to set
     */
    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * @param borrowerStatus the borrowerStatus to set
     */
    public void setBorrowerStatus(String borrowerStatus) {
        this.borrowerStatus = borrowerStatus;
    }

    /**
     * Creates a new <code>Event</code> related to an item.
     *
     * @param type           the type of this event
     * @param borrowerStatus the status of the person initiating the event
     */
    public EsEvent(String eventId, Date beginDate, Date endDate, EventType type, String borrowerStatus) {
        this.type = type;
        this.borrowerStatus = borrowerStatus;
        this.eventId = eventId;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public EsEvent() {
    }

    public Long calculateDurationInDays() {
        long durationInMilliseconds;
        if (endDate != null)
            durationInMilliseconds = Math.abs(beginDate.getTime() - endDate.getTime());
        else
            durationInMilliseconds = System.currentTimeMillis() - beginDate.getTime();
        return durationInMilliseconds / dayInMillis;
    }

    /**
     * sets the Date of the event
     *
     * @param beginDate the new Date
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * relates to the end <code>Event</code>-object, for example the return to a
     * loan event.
     *
     * @param endDate the event ending the started process
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * returns the date (yyyy-MM-dd HH:mm) of the event
     *
     * @return date the date of the event
     */
    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    /**
     * returns the type of the event (loan, return, request, hold, inventory,
     * deletion or CALD)
     *
     * @return type the type of event
     */
    public EventType getType() {
        return type;
    }

    /**
     * returns the status of the person initiating the event (student,
     * non-student member of university, external user, research faculty, other)
     *
     * @return status the status of the person initiating the event
     */
    public String getBorrowerStatus() {
        return borrowerStatus;
    }

    /**
     * allows for a comparison of two events with respect to their timestamps.
     * Allows for the ordering of events according to the timestamps.
     *
     * @return difference +1 of event is after the other one, -1 if it before.
     */
    public int compareTo(EsEvent other) {
        return this.beginDate.compareTo(other.beginDate);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

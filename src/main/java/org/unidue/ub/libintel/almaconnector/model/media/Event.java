package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representation object of one event from one of the three groups loans (loan
 * and return) requests (request and hold) and stock (inventory and deletion).
 *
 * @author Frank L\u00FCtzenkirchen
 * @version 1
 */
@Data
public class Event implements Comparable<Event> {

    @JsonBackReference
    private Item item;

    @Field(type = FieldType.Nested, includeInParent = true)
    private Event endEvent;

    @Field(type = FieldType.Date)
    private Date date;

    private String type;

    private String borrowerStatus;

    @Field(type = FieldType.Integer)
    private int delta;

    @Field(type = FieldType.Long)
    private long duration;

    /**
     * Creates a new <code>Event</code> related to an item.
     *
     * @param item           the item related to this event
     * @param date           the date, wehn this events happens
     * @param type           the type of this event
     * @param borrowerStatus the status of the person initiating the event
     * @param delta          the change of the overall number of items in a particular
     *                       state upon this event (a loan increases the number of loaned
     *                       items by +1)
     */
    public Event(Item item, Date date, String type, String borrowerStatus, int delta) {
        this.type = type;
        this.delta = delta;
        this.borrowerStatus = borrowerStatus;
        this.date = date;
        this.item = item;
        item.addEvent(this);
    }

    private static final SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMddHHmm");

    private final static long dayInMillis = 86400000L;
    private static SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    /**
     * allows for a comparison of two events with respect to their timestamps.
     * Allows for the ordering of events according to the timestamps.
     *
     * @return difference +1 of event is after the other one, -1 if it before.
     */
    public int compareTo(Event other) {
        return this.date.compareTo(other.date);
    }
}

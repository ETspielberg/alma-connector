package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.text.ParseException;
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
	private long time;

	@Field(type = FieldType.Date)
	private String date;

	@Field(type = FieldType.Integer)
	private int year;

	private String type;

	private String borrowerStatus;

	@Field(type = FieldType.Integer)
	private int sorter;

	@Field(type = FieldType.Integer)
	private int delta;
	
	private String itemId;

	@Field(type = FieldType.Long)
	private long duration;

	/**
	 * Creates a new <code>Event</code> related to an item.
	 * 
	 * 
	 * @param item
	 *            the item related to this event
	 * @param date
	 *            the date, wehn this events happens
	 * @param hour
	 *            the hour, wehn this events happens
	 * @param type
	 *            the type of this event
	 * @param borrowerStatus
	 *            the status of the person initiating the event
	 * @param sorter
	 *            an additional sorter to allow sorting of events in the case of
	 *            multiple events at the same time
	 * @param delta
	 *            the change of the overall number of items in a particular
	 *            state upon this event (a loan increases the number of loaned
	 *            items by +1)
	 */
	public Event(Item item, String date, String hour, String type, String borrowerStatus, int sorter, int delta) {
		this.type = type;
		this.sorter = sorter;
		this.delta = delta;
		this.borrowerStatus = borrowerStatus;

		setTimeFields(date, hour);

		this.item = item;
		item.addEvent(this);
	}

	private static final SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMddHHmm");

	private final static long dayInMillis = 86400000L;
	private static SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private void setTimeFields(String date, String hour){
		if (date.length() > 4)
		this.year = Integer.parseInt(date.substring(0, 4));
		else year = 2000;

		StringBuilder hourBuilder = new StringBuilder(hour);
		while (hourBuilder.length() < 4)
			hourBuilder.insert(0, "0");
		hour = hourBuilder.toString();

		synchronized (formatIn) {
			try {
			Date d = formatIn.parse(date + hour);
			this.date = formatOut.format(d);
			this.time = d.getTime();
			} catch (ParseException pe) {
				pe.getStackTrace();
			}
		}
	}

	/**
	 * allows for a comparison of two events with respect to their timestamps.
	 * Allows for the ordering of events according to the timestamps.
	 *
	 * @return difference +1 of event is after the other one, -1 if it before.
	 */
	public int compareTo(Event other) {
		if (this.time > other.time)
			return 1;
		else if (this.time < other.time)
			return -1;
		else
			return this.sorter - other.sorter;
	}
}

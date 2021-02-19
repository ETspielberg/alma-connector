package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
	 * @return the sorter
	 */
	public int getSorter() {
		return sorter;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param borrowerStatus the borrowerStatus to set
	 */
	public void setBorrowerStatus(String borrowerStatus) {
		this.borrowerStatus = borrowerStatus;
	}

	/**
	 * @param sorter the sorter to set
	 */
	public void setSorter(int sorter) {
		this.sorter = sorter;
	}

	/**
	 * @param delta the delta to set
	 */
	public void setDelta(int delta) {
		this.delta = delta;
	}

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
	 * @exception ParseException
	 *                exception parsing the date field
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
	
	public Event(String itemId, String date, String hour, String type, String borrowerStatus, int sorter) {
		this.itemId = itemId;
		this.type = type;
		this.sorter = sorter;
		if (type.equals("loan") || type.equals("request") || type.equals("inventory") || type.equals("cald"))
			this.delta = 1;
		else if (type.equals("return") || type.equals("hold") || type.equals("deletion") || type.equals("caldDelivery"))
			this.delta = -1;
		else
			this.delta = 0;
		this.borrowerStatus = borrowerStatus;
		setTimeFields(date, hour);
	}
	
	public Event() {}

	private static SimpleDateFormat formatIn = new SimpleDateFormat("yyyyMMddHHmm");

	private final static long dayInMillis = 86400000L;
	private static SimpleDateFormat formatOut = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private void setTimeFields(String date, String hour){
		if (date.length() > 4)
		this.year = Integer.valueOf(date.substring(0, 4));
		else year = 2000;

		while (hour.length() < 4)
			hour = "0" + hour;

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

	public void calculateDuration(){
		long durationInMilliseconds;
		if (endEvent != null)
			if (endEvent.getTime() != 0)
			durationInMilliseconds = endEvent.getTime() - time;
		else durationInMilliseconds = 0;
		else
			durationInMilliseconds = System.currentTimeMillis() - time;
		duration = durationInMilliseconds / dayInMillis;
	}



	/**
	 * retrieves the item related to this event.
	 *
	 * @return item the item related to this event.
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * sets the Date of the event
	 *
	 * @param newDate
	 *            the new Date
	 */
	public void setDate(String newDate) {
		this.date = newDate;
	}

	/**
	 * relates to the end <code>Event</code>-object, for example the return to a
	 * loan event.
	 *
	 * @param endEvent
	 *            the event ending the started process
	 */
	public void setEndEvent(Event endEvent) {
		this.endEvent = endEvent;
	}

	/**
	 * returns the <code>Event</code>-object, for example the return to a loan
	 * event.
	 *
	 * @return endEvent the event ending the started process
	 */
	public Event getEndEvent() {
		return endEvent;
	}

	/**
	 * returns the year of the event
	 *
	 * @return year the year of the event
	 */
	public int getYear() {
		return year;
	}

	/**
	 * returns the timestamp (in milliseconds) of the event.
	 *
	 * @return time the timestamp of the event
	 */
	public long getTime() {
		return time;
	}

	/**
	 * returns the date (yyyy-MM-dd HH:mm) of the event
	 *
	 * @return date the date of the event
	 */
	public String getDate() {
		return date;
	}

	/**
	 * returns the type of the event (loan, return, request, hold, inventory,
	 * deletion or CALD)
	 *
	 * @return type the type of event
	 */
	public String getType() {
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

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * returns the change of the overall number of items in a particular state
	 * upon this event (a loan increases the number of loaned items by +1)
	 *
	 * @return delta the overall number of items in a particular state upon this
	 *         event
	 */
	public int getDelta() {
		return delta;
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

	/**
	 * @return the recKey
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the recKey to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
}

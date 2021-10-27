package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.unidue.ub.libintel.almaconnector.model.hook.HookEventTypes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representation object of one item
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class Item {

	final static String UNKNOWN = "???";

	private String collection;

	@Field(analyzer = "keyword")
	private String shelfmark;

	private String subLibrary;

	private String material;

	private String itemStatus;

	private String processStatus;

	@Field(type=FieldType.Date)
	private Date inventoryDate;

	@Field(type=FieldType.Date)
	private Date deletionDate;

	private String itemId;

	private String noteOpac;

	@Field(analyzer = "keyword")
	private String barcode;

	@JsonManagedReference
	@Field(type = FieldType.Nested, includeInParent = true)
	private List<Event> events = new ArrayList<>();

	public Item(org.unidue.ub.alma.shared.bibs.Item almaItem, Date inventoryDate) {
		this.itemId = almaItem.getItemData().getPid();
		this.subLibrary = almaItem.getItemData().getLibrary().getValue();
		this.material = almaItem.getItemData().getPhysicalMaterialType().getValue();
		this.inventoryDate = inventoryDate;
		this.shelfmark = almaItem.getItemData().getAlternativeCallNumber();
		this.noteOpac = almaItem.getItemData().getPublicNote();
		this.barcode = almaItem.getItemData().getBarcode();
		this.events.add(new Event(this, inventoryDate, EventTypes.INVENTORY.name(), "library", +1));
	}



	public String getNoteOpac() {
		return noteOpac;
	}

	public void setNoteOpac(String noteOpac) {
		this.noteOpac = noteOpac;
	}

	/**
	 * returns the key in the Aleph database of this item
	 *
	 * @return recKey the key in the database
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * sets the key in the Aleph database of this item
	 *
	 * @param itemId
	 *            the key in the database
	 */
	public void setItemId(String itemId) {
		this.itemId = (itemId.length() > 15) ? itemId.substring(0,15) : itemId;
	}

	/**
	 * returns the status of this particular item
	 *
	 * @return itemStatus the status of the item
	 */
	public String getItemStatus() {
		return itemStatus;
	}

	/**
	 * returns the process status of this particular item
	 *
	 * @return processStatus the process status
	 */
	public String getProcessStatus() {
		return processStatus;
	}

	/**
	 * returns the collection this item belongs to
	 *
	 * @return collection the collection this item belongs to
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * returns the type of material of this item
	 *
	 * @return material the type of material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * returns the shelfmark of this particular item
	 *
	 * @return callNo the shelfmark
	 */
	public String getShelfmark() {
		return shelfmark;
	}

	/**
	 * returns the date when this item was inventoried
	 *
	 * @return inventoryDate the date of inventory
	 */
	public Date getInventoryDate() {
		return inventoryDate;
	}

	/**
	 * returns the date this item was de-inventoried
	 *
	 * @return deletionDate the date of de-inventory
	 */
	public Date getDeletionDate() {
		return deletionDate;
	}

	/**
	 * adds an <code>Event</code>-object to the list of events associated with
	 * this item.
	 *
	 * @param event
	 *            an <code>Event</code>-object
	 */
	public void addEvent(Event event) {
		events.add(event);
	}

	/**
	 * returns all events associated with this item
	 *
	 * @return events list of events
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * @return the subLibrary
	 */
	public String getSubLibrary() {
		return subLibrary;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	/**
	 * @param shelfmark the shelfmark to set
	 */
	public void setShelfmark(String shelfmark) {
		this.shelfmark = shelfmark;
	}

	/**
	 * @param subLibrary the subLibrary to set
	 */
	public void setSubLibrary(String subLibrary) {
		this.subLibrary = subLibrary;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @param itemStatus the itemStatus to set
	 */
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}

	/**
	 * @param processStatus the processStatus to set
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	/**
	 * @param inventoryDate the inventoryDate to set
	 */
	public void setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	/**
	 * @param deletionDate the deletionDate to set
	 */
	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}


    public void delete(Date date) {
		this.setDeletionDate(date);
		for (Event event: events) {
			if (event.getType().equals("inventory")) {
				event.setEndEvent(new Event(this, date, "deletion", "library", -1));
			}
		}
    }

	public void update(org.unidue.ub.alma.shared.bibs.Item almaItem) {
		this.itemId = almaItem.getItemData().getPid();
		this.subLibrary = almaItem.getItemData().getLibrary().getValue();
		this.material = almaItem.getItemData().getPhysicalMaterialType().getValue();
		this.shelfmark = almaItem.getItemData().getAlternativeCallNumber();
		this.noteOpac = almaItem.getItemData().getPublicNote();
		this.barcode = almaItem.getItemData().getBarcode();
	}

	public void closeLoan(Event endEvent) {
		for (Event event: this.events) {
			if (HookEventTypes.LOAN_CREATED.name().equals(event.getType()) && event.getEndEvent() == null)
				event.setEndEvent(endEvent);
		}
	}
}

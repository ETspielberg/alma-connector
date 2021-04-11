package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
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
	private String inventoryDate;

	@Field(type=FieldType.Date)
	private String deletionDate;

	@Field(type=FieldType.Float)
	private String price;

	private String itemId;

	@Field(analyzer = "keyword")
	private String etat;

	private String noteOpac;

	@Field(analyzer = "keyword")
	private String barcode;

	@JsonManagedReference
	@Field(type = FieldType.Nested, includeInParent = true)
	private List<Event> events = new ArrayList<Event>();

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param collection
	 *            the collection this item belongs to
	 * @param shelfmark
	 *            the shelfmark of this item
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 * @param itemStatus
	 *            the status of this item
	 * @param processStatus
	 *            the process status of this item
	 * @param inventoryDate
	 *            the date this item was inventoried
	 * @param deletionDate
	 *            the date when this item was de-inventoried
	 * @param price
	 *            the price of this item
	 */
	public Item(String itemId, String collection, String shelfmark, String subLibrary, String material,
                String itemStatus, String processStatus, String inventoryDate, String deletionDate, String price) {
		this(itemId, subLibrary, material, inventoryDate, deletionDate, price);

		if ((itemStatus != null) && !itemStatus.trim().isEmpty())
			this.itemStatus = itemStatus;
		if ((processStatus != null) && !processStatus.trim().isEmpty())
			this.processStatus = processStatus;

		if ((collection != null) && !collection.isEmpty())
			this.collection = collection.trim();

		if ((shelfmark != null) && !shelfmark.isEmpty())
			this.shelfmark = shelfmark;
		this.itemId = itemId;
	}

	public Item(String itemId, String collection, String shelfmark, String subLibrary, String material,
                String itemStatus, String processStatus, String inventoryDate, String deletionDate, String price, String noteOpac) {
		this(itemId, collection, shelfmark, subLibrary, material,itemStatus, processStatus, inventoryDate, deletionDate, price);
		this.noteOpac = noteOpac;
	}

	public Item(String itemId, String collection, String shelfmark, String subLibrary, String material,
                String itemStatus, String processStatus, String inventoryDate, String deletionDate, String price, String noteOpac, String barcode) {
		this(itemId, collection, shelfmark, subLibrary, material,itemStatus, processStatus, inventoryDate, deletionDate, price);
		this.noteOpac = noteOpac;
		this.barcode = barcode;
	}

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 * @param inventoryDate
	 *            the date this item was inventoried
	 * @param deletionDate
	 *            the date when this item was de-inventoried
	 * @param price
	 *            the price of this item
	 */
	public Item(String itemId, String subLibrary, String material, String inventoryDate, String deletionDate,
                String price) {
		this.itemId = (itemId.length() > 15) ? itemId.substring(0,15) : itemId;
		this.subLibrary = subLibrary;
		this.material = material.trim();
		this.inventoryDate = inventoryDate;
		this.deletionDate = deletionDate;
		this.collection = UNKNOWN;
		this.shelfmark = UNKNOWN;
		this.itemStatus = "xx";
		this.price = price;
	}

	public Item(org.unidue.ub.alma.shared.bibs.Item almaItem) {
		this.itemId = almaItem.getItemData().getPid();
		this.subLibrary = almaItem.getItemData().getLibrary().getValue();
		this.material = almaItem.getItemData().getPhysicalMaterialType().getValue();
		// this.inventoryDate = almaItem.getItemData().getInventoryDate();
		this.shelfmark = almaItem.getItemData().getAlternativeCallNumber();
		this.price = almaItem.getItemData().getInventoryPrice();
		this.noteOpac = almaItem.getItemData().getPublicNote();
		this.barcode = almaItem.getItemData().getBarcode();

	}

	/**
	 * Creates a new <code>Item</code>.
	 * 
	 * 
	 * @param subLibrary
	 *            the sublibrary this item is located in
	 * @param material
	 *            the type of material of this item (book, cd-rom etc.)
	 */
	Item(String itemId, String subLibrary, String material) {
		this.itemId = (itemId.length() > 15) ? itemId.substring(0,15) : itemId;
		this.subLibrary = subLibrary;
		this.material = material.trim();
		this.collection = UNKNOWN;
		this.shelfmark = UNKNOWN;
		this.itemStatus = "xx";
	}
	
	public Item() {
		
	}



	public String getNoteOpac() {
		return noteOpac;
	}

	public void setNoteOpac(String noteOpac) {
		this.noteOpac = noteOpac;
	}

	/**
	 * returns the code of the budget this item was paid for.
	 *
	 * @return etat the budget code
	 */
	public String getEtat() {
		return etat;
	}

	/**
	 * sets the code of the budget this item was paid for.
	 *
	 * @param etat
	 *            the budget code
	 */
	public void setEtat(String etat) {
		this.etat = etat;
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
	public String getInventoryDate() {
		return inventoryDate;
	}

	/**
	 * returns the date this item was de-inventoried
	 *
	 * @return deletionDate the date of de-inventory
	 */
	public String getDeletionDate() {
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
	 * returns the price of this item
	 *
	 * @return price the price
	 */
	public String getPrice() {
		return price;
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
	public void setInventoryDate(String inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	/**
	 * @param deletionDate the deletionDate to set
	 */
	public void setDeletionDate(String deletionDate) {
		this.deletionDate = deletionDate;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
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

	@JsonIgnore
	public int getItemSequence() {
		String sequenceString = "";
		if (itemId.length() > 9)
			sequenceString= itemId.substring(9);
		else
			sequenceString = "0";
		return Integer.parseInt(sequenceString);
	}
}

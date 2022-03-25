package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import org.unidue.ub.alma.shared.bibs.Item;
import org.unidue.ub.alma.shared.bibs.MarcDatafield;
import org.unidue.ub.alma.shared.bibs.MarcSubfield;
import org.unidue.ub.libintel.almaconnector.model.EventType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representation object of one item
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
public class EsItem {

	private String collection;

	private String shelfmark;

	private String subLibrary;

	private String material;

	private String itemStatus;

	private String processStatus;

	private Date inventoryDate;

	private Date deletionDate;

	private String itemId;

	private String noteOpac;

	private String barcode;

	private List<EsEvent> esEvents = new ArrayList<>();

	public EsItem(Item almaItem, Date inventoryDate) {
		this.itemId = almaItem.getItemData().getPid();
		this.subLibrary = almaItem.getItemData().getLibrary().getValue();
		this.collection = almaItem.getItemData().getLocation().getValue();
		this.material = almaItem.getItemData().getPhysicalMaterialType().getValue();
		this.inventoryDate = inventoryDate;
		this.shelfmark = almaItem.getItemData().getAlternativeCallNumber();
		this.noteOpac = almaItem.getItemData().getPublicNote();
		this.barcode = almaItem.getItemData().getBarcode();
		this.esEvents.add(new EsEvent(itemId, inventoryDate, null, EventType.INVENTORY, "library"));
	}

    public EsItem() {}


	public EsItem(Item item) {
		this.shelfmark = item.getItemData().getAlternativeCallNumber();
		this.collection = item.getItemData().getLocation().getValue();
		this.itemId = item.getItemData().getPid();
		this.subLibrary = item.getItemData().getLibrary().getValue();
		this.barcode = item.getItemData().getBarcode();
		if (item.getItemData().getInventoryDate() != null)
			this.inventoryDate = item.getItemData().getInventoryDate();
		else
			this.inventoryDate = item.getItemData().getArrivalDate();
		this.esEvents.add(new EsEvent(this.itemId, this.inventoryDate, null, EventType.INVENTORY, ""));
		this.processStatus = item.getItemData().getProcessType().getValue();
		this.itemStatus = item.getItemData().getBaseStatus().getValue();
		this.noteOpac = item.getItemData().getPublicNote();
		this.material = item.getItemData().getPhysicalMaterialType().getValue();

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
	 * @param esEvent
	 *            an <code>Event</code>-object
	 */
	public void addEvent(EsEvent esEvent) {
		esEvents.add(esEvent);
	}

	/**
	 * returns all events associated with this item
	 *
	 * @return events list of events
	 */
	public List<EsEvent> getEvents() {
		return esEvents;
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
	 * @param esEvents the events to set
	 */
	public void setEvents(List<EsEvent> esEvents) {
		this.esEvents = esEvents;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}


    public void delete(Date date) {
		this.setDeletionDate(date);
		for (EsEvent esEvent : esEvents) {
			if (esEvent.getType().equals(EventType.INVENTORY)) {
				esEvent.setEndDate(date);
			}
		}
    }

	public boolean update(Item almaItem) {
		boolean isChanged = false;
		if (!this.itemId.equals(almaItem.getItemData().getPid())) {
			this.itemId = almaItem.getItemData().getPid();
			isChanged = true;
		}
		if (!this.subLibrary.equals(almaItem.getItemData().getLibrary().getValue())) {
			this.subLibrary = almaItem.getItemData().getLibrary().getValue();
			isChanged = true;
		}
		if (!this.material.equals(almaItem.getItemData().getPhysicalMaterialType().getValue())) {
			this.material = almaItem.getItemData().getPhysicalMaterialType().getValue();
			isChanged = true;
		}
		if (!this.shelfmark.equals(almaItem.getItemData().getAlternativeCallNumber())) {
			this.shelfmark = almaItem.getItemData().getAlternativeCallNumber();
			isChanged = true;
		}
		if (!this.noteOpac.equals(almaItem.getItemData().getPublicNote())) {
			this.noteOpac = almaItem.getItemData().getPublicNote();
			isChanged = true;
		}
		if (!this.barcode.equals(almaItem.getItemData().getBarcode())) {
			this.barcode = almaItem.getItemData().getBarcode();
			isChanged = true;
		}
		return isChanged;
	}

	public void closeLoan(Date closeDate, String loanId) {
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.LOAN.equals(esEvent.getType()) && esEvent.getEndDate() == null && esEvent.getEventId().equals(loanId)) {
				esEvent.setEndDate(closeDate);
				return;
			}
		}
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.LOAN.equals(esEvent.getType()) && esEvent.getEndDate() == null) {
				esEvent.setEndDate(closeDate);
				return;
			}
		}
	}

	public void closeRequest(Date date, String requestId) {
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.REQUEST.equals(esEvent.getType()) && esEvent.getEndDate() == null && esEvent.getEventId().equals(requestId)) {
				esEvent.setEndDate(date);
				return;
			}
		}
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.REQUEST.equals(esEvent.getType()) && esEvent.getEndDate() == null) {
				esEvent.setEndDate(date);
				return;
			}
		}
	}

	public void closeCald(Date date, String requestId) {
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.CALD.equals(esEvent.getType()) && esEvent.getEndDate() == null && esEvent.getEventId().equals(requestId)) {
				esEvent.setEndDate(date);
				return;
			}
		}
		for (EsEvent esEvent : this.esEvents) {
			if (EventType.CALD.equals(esEvent.getType()) && esEvent.getEndDate() == null) {
				esEvent.setEndDate(date);
				return;
			}
		}
	}
}

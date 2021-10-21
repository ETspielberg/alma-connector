package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of one manifestation characterized by given document number
 * and a specific callNo (shelfmark). One <code>Document</code> can contain
 * several <code>Item</code> objects and gives access to the <code>Event</code>
 * objects connected to these items. It also holds the Bibliographic data in
 * MAB-xml format
 * 
 * @author Frank L\u00FCtzenkirchen, Eike Spielberg
 * @version 1
 */
@Document(indexName = "manifestation_v1")
public class Manifestation implements Cloneable, Comparable<Manifestation> {

	private final static Pattern editionFinder = Pattern.compile(".*\\((\\d+)\\).*");

	@Field(analyzer = "keyword")
	private String almaId = "";

	@Id
	@Field(analyzer = "keyword")
	private String titleID = "";

	@Field(analyzer = "keyword")
	private String shelfmark = "";

	@Field(analyzer = "keyword")
	private String shelfmarkBase = "";

	@Field(type = FieldType.Nested, includeInParent = true)
	private List<Item> items = new ArrayList<>();

	@Field(type = FieldType.Integer)
	private int edition = 1;

	@Field(type = FieldType.Nested, includeInParent = true)
	private BibliographicInformation bibliographicInformation;

	public Manifestation() {
	}

	public Manifestation(BibWithRecord bib) {
		this.titleID = bib.getMmsId();
		this.almaId = bib.getMmsId();
		this.bibliographicInformation = new BibliographicInformation(bib);
	}

	public Manifestation(String titleID) {
		this.titleID = titleID;
	}

	public Manifestation(String titleID, String almaId) {
		this.titleID = titleID;
		this.almaId = almaId;
	}


	public void setTitleID(String titleID) {
		this.titleID = titleID;
	}

	public String getTitleID() {
		return titleID;
	}

	public void setShelfmark(String shelfmark) {
		this.shelfmark = shelfmark;
	}

	public String getShelfmark() {
		return shelfmark;
	}

	public void setShelfmarkBase(String shelfmarkBase) {
		this.shelfmarkBase = shelfmarkBase;
	}

	public String getShelfmarkBase() {
		return shelfmarkBase;
	}

	public int getEdition() {
		return edition;
	}

	public static Pattern getEditionFinder() {
		return editionFinder;
	}

	public String getAlmaId() {
		return almaId;
	}

	public void setAlmaId(String almaId) {
		this.almaId = almaId;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public void setEdition(int edition) {
		this.edition = edition;
	}

	public void addItem(Item item) {
		items.add(item);
		addItemShelfmarkIfNew(item);
	}

	public void addItems(List<Item> items) {
		for (Item item : items) {
			addItem(item);
		}
	}


	public List<Item> getItems() {
		return items;
	}

	public Item getItem(String itemId) {
		for (Item item : items)
			if (item.getItemId().equals(itemId))
				return item;
		return null;
	}


	public void setItmes(List<Item> items) {
		this.items = items;
	}

	public BibliographicInformation getBibliographicInformation() {
		return bibliographicInformation;
	}

	public void setBibliographicInformation(BibliographicInformation bibliographicInformation) {
		this.bibliographicInformation = bibliographicInformation;
	}

	@JsonIgnore
	public List<Event> getEvents() {
		List<Event> events = new ArrayList<>();
		for (Item item : getItems())
			events.addAll(item.getEvents());
		Collections.sort(events);
		return events;
	}

	@Override
	public boolean equals(Object other) {
		return shelfmark.equals(((Manifestation) other).shelfmark);
	}

	@Override
	public int hashCode() {
		return titleID.trim().hashCode();
	}

	public Manifestation clone() {
		Manifestation clone = new Manifestation(shelfmark);
		for (Item item : items)
			clone.addItem(item);
		return clone;
	}

	@JsonIgnore
	public String[] getShelfmarks() {
		return shelfmark.split(", ");
	}

	@JsonIgnore
	public List<String> getBarcodes() {
		List<String> barcodes = new ArrayList<>();
		for (Item item : this.items) {
			if (item.getBarcode() != null && !"".equals(item.getBarcode()))
				barcodes.add(item.getBarcode());
		}
		return barcodes;
	}


	private void addItemShelfmarkIfNew(Item item) {
		String shelfmarkItem = item.getShelfmark().replaceAll("\\+\\d+", "");
		if ((shelfmarkItem == null) || (shelfmarkItem.equals(Item.UNKNOWN)) || shelfmarkItem.isEmpty()) {
			return;
		} else if (!shelfmark.contains(shelfmarkItem))
			addShelfmark(shelfmarkItem);
	}

	private void addShelfmark(String shelfmark) {
		if (!this.shelfmark.isEmpty())
			this.shelfmark += "; ";
		this.shelfmark += shelfmark;
		buildEdition(this.shelfmark);
		buildShelfmarkBase(this.shelfmark);
	}

	private void buildShelfmarkBase(String shelfmark) {
		shelfmarkBase = editionFinder.matcher(shelfmark).matches() ? shelfmark.replaceAll("\\((\\d+)\\)", "") : shelfmark;
	}

	private void buildEdition(String shelfmark) {
		Matcher m = editionFinder.matcher(shelfmark);
		try {
			edition = m.matches() ? Integer.valueOf(m.group(1)) : 1;
		} catch (Exception exception) {
			edition = 1;
		}
	}

	public int compareTo(Manifestation other) {
		if (this.edition > other.getEdition())
			return 1;
		else return -1;
	}

	public boolean contains(String shelfmark) {
		return this.shelfmark.contains(shelfmark);
}
}
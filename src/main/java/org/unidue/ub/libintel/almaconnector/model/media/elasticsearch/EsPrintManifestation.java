package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;
import org.unidue.ub.alma.shared.bibs.MarcDatafield;

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
public class EsPrintManifestation implements Cloneable, Comparable<EsPrintManifestation> {

	private String almaId = "";

	private String titleID = "";

	private String shelfmark = "";

	private String shelfmarkBase = "";

	private List<EsItem> esItems = new ArrayList<>();

	private int edition = 1;

	private EsBibliographicInformation esBibliographicInformation;

	private List<GetterAlert> alerts = new ArrayList<>();

	public EsPrintManifestation() {
	}

	public EsPrintManifestation(BibWithRecord bib) {
			this.titleID = bib.getMmsId();
			this.almaId = bib.getMmsId();
			this.esBibliographicInformation = new EsBibliographicInformation(bib);
	}

	public EsPrintManifestation(String titleID) {
		this.titleID = titleID;
	}

	public EsPrintManifestation(String titleID, String almaId) {
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

	public String getAlmaId() {
		return almaId;
	}

	public void setAlmaId(String almaId) {
		this.almaId = almaId;
	}

	public void setItems(List<EsItem> esItems) {
		this.esItems = esItems;
	}

	public void setEdition(int edition) {
		this.edition = edition;
	}

	public List<GetterAlert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<GetterAlert> alerts) {
		this.alerts = alerts;
	}

	public void addAlert(GetterAlert alert) {
		if (this.alerts == null)
			this.alerts = new ArrayList<>();
		this.alerts.add(alert);
	}

	public void addItem(EsItem esItem) {
		esItems.add(esItem);
		addItemShelfmarkIfNew(esItem);
	}

	public void addItems(List<EsItem> esItems) {
		for (EsItem esItem : esItems) {
			addItem(esItem);
		}
	}

	public List<EsItem> getItems() {
		return esItems;
	}

	public EsItem getItem(String itemId) {
		for (EsItem esItem : esItems)
			if (esItem.getItemId().equals(itemId))
				return esItem;
		return null;
	}

	public EsItem getItemByShelfmark(String shelfmark) {
		if (shelfmark == null) return null;
		for (EsItem esItem : esItems)
			if (shelfmark.equals(esItem.getShelfmark()))
				return esItem;
		return null;
	}

	public EsItem getItemByBarcode(String barcode) {
		if (barcode == null) return null;
		for (EsItem esItem : esItems)
			if (barcode.equals(esItem.getBarcode()))
				return esItem;
		return null;
	}

	public EsBibliographicInformation getBibliographicInformation() {
		return esBibliographicInformation;
	}

	public void setBibliographicInformation(EsBibliographicInformation esBibliographicInformation) {
		this.esBibliographicInformation = esBibliographicInformation;
	}

	@JsonIgnore
	public List<EsEvent> getEvents() {
		List<EsEvent> esEvents = new ArrayList<>();
		for (EsItem esItem : getItems())
			esEvents.addAll(esItem.getEvents());
		Collections.sort(esEvents);
		return esEvents;
	}

	@Override
	public boolean equals(Object other) {
		return shelfmark.equals(((EsPrintManifestation) other).shelfmark);
	}

	@Override
	public int hashCode() {
		return titleID.trim().hashCode();
	}

	public EsPrintManifestation clone() {
		EsPrintManifestation clone = new EsPrintManifestation(shelfmark);
		for (EsItem esItem : esItems)
			clone.addItem(esItem);
		return clone;
	}


	public String[] getShelfmarks() {
		return shelfmark.split(", ");
	}


	public List<String> getBarcodes() {
		List<String> barcodes = new ArrayList<>();
		for (EsItem esItem : this.esItems) {
			if (esItem.getBarcode() != null && !"".equals(esItem.getBarcode()))
				barcodes.add(esItem.getBarcode());
		}
		return barcodes;
	}


	private void addItemShelfmarkIfNew(EsItem esItem) {
		String shelfmarkItem = esItem.getShelfmark().replaceAll("\\+\\d+", "");
		if ((shelfmarkItem == null) || (shelfmarkItem.equals("???")) || shelfmarkItem.isEmpty()) {
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
		Pattern editionFinder = Pattern.compile(".*\\((\\d+)\\).*");
		shelfmarkBase = editionFinder.matcher(shelfmark).matches() ? shelfmark.replaceAll("\\((\\d+)\\)", "") : shelfmark;
	}

	private void buildEdition(String shelfmark) {
		Pattern editionFinder = Pattern.compile(".*\\((\\d+)\\).*");
		Matcher m = editionFinder.matcher(shelfmark);
		try {
			edition = m.matches() ? Integer.parseInt(m.group(1)) : 1;
		} catch (Exception exception) {
			edition = 1;
		}
	}

	public int compareTo(EsPrintManifestation other) {
		if (this.edition > other.getEdition())
			return 1;
		else return -1;
	}

	public boolean contains(String shelfmark) {
		return this.shelfmark.contains(shelfmark);
}

    public EsItem findCorrespindingItem(org.unidue.ub.alma.shared.bibs.Item almaItem) {
		EsItem esItem = this.getItem(almaItem.getItemData().getPid());
		if (esItem == null)
			esItem = this.getItemByBarcode(almaItem.getItemData().getBarcode());
		if (esItem == null)
			esItem = this.getItemByShelfmark(almaItem.getItemData().getAlternativeCallNumber());
		return esItem;
    }
}
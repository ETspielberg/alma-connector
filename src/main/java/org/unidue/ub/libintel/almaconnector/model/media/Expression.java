package org.unidue.ub.libintel.almaconnector.model.media;

import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.*;

import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of one expression consisting of different manifestations
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class Expression implements Cloneable {

	private String id;

	private String shelfmarkBase;

	private List<EsPrintManifestation> esPrintManifestations = new ArrayList<>();

	private  List<EsElectronicManifestation> esElectronicManifestations = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public List<EsPrintManifestation> getEsPrintManifestations() {
		return esPrintManifestations;
	}

	public void setEsPrintManifestations(List<EsPrintManifestation> esPrintManifestations) {
		this.esPrintManifestations = esPrintManifestations;
	}

	public List<EsElectronicManifestation> getEsElectronicManifestations() {
		return esElectronicManifestations;
	}

	public void setEsElectronicManifestations(List<EsElectronicManifestation> esElectronicManifestations) {
		this.esElectronicManifestations = esElectronicManifestations;
	}

	/**
	 * creates a new <code>Work</code>-object with the given basic shelfmark
	 *
	 * @param shelfmarkBase
	 *            the basic shelfmark of the work
	 */

	public Expression(String shelfmarkBase) {
		this.shelfmarkBase = shelfmarkBase;
	}

	/**
	 * returns the basic shelfmark for this work
	 *
	 * @return shelfmarkBase the basic shelfmark
	 */
	public String getShelfmarkBase() {
		return shelfmarkBase;
	}

	/**
	 * sets the basic shelfmark for this work
	 *
	 * @param shelfmarkBase
	 *            the basic shelfmark
	 */
	public void setShelfmarkBase(String shelfmarkBase) {
		this.shelfmarkBase = shelfmarkBase;
	}

	/**
	 * adds a document to this work
	 *
	 * @param document
	 *            the document to be added
	 */
	public void addManifestation(EsPrintManifestation document) {
		esPrintManifestations.add(document);
	}

	/**
	 * checks whether a document is already in this work
	 *
	 * @return boolean true if work contains document
	 * @param esPrintManifestation
	 *            the document to be tested
	 */
	public boolean contains(EsPrintManifestation esPrintManifestation) {
		return esPrintManifestation.getShelfmarkBase().equals(this.shelfmarkBase);
	}

	/**
	 * returns all documents of this work
	 *
	 * @return documents the list of documents
	 */

	public List<EsPrintManifestation> getManifestations() {
		return esPrintManifestations;
	}

	/**
	 * returns the events of all items belonging to this work
	 *
	 * @return events the list of events
	 */
	public List<EsEvent> getEvents() {
		List<EsEvent> esEvents = new ArrayList<>();
		for (EsPrintManifestation esPrintManifestation : esPrintManifestations) {
			List<EsEvent> eventsManifestation = esPrintManifestation.getEvents();
			esEvents.addAll(eventsManifestation);
		}
		Collections.sort(esEvents);
		return esEvents;
	}

	/**
	 * returns the items belonging to this work
	 *
	 * @return items the list of items
	 */
	public List<EsItem> getItems() {
		List<EsItem> esItems = new ArrayList<>();
		for (EsPrintManifestation document : esPrintManifestations) {
			esItems.addAll(document.getItems());
		}
		return esItems;
	}

	/**
	 * returns the document with the specified document number from this work
	 *
	 * @param titleID
	 *            the document number of a document within the work
	 * @return document the document with the corresponding document number
	 */
	public EsPrintManifestation getDocument(String titleID) {
		for (EsPrintManifestation document : esPrintManifestations)
			if (document.getTitleID().equals(titleID))
				return document;
		return null;
	}

	/**
	 * returns compares two works by their basic shelfmarks
	 *
	 * @param other
	 *            another work
	 * @return document true, if the basic shelfmarks are identical
	 */
	@Override
	public boolean equals(Object other) {
		return shelfmarkBase.equals(((Expression) other).shelfmarkBase);
	}

	/**
	 * returns a hash code for this work
	 *
	 * @return haschCode a hash for this work
	 */
	@Override
	public int hashCode() {
		return shelfmarkBase.hashCode();
	}
	
	/**
	 * @return the bibliographicInformation
	 */
	public EsBibliographicInformation getBibliographicInformation() {
		Collections.sort(esPrintManifestations);
		EsBibliographicInformation information = esPrintManifestations.get(esPrintManifestations.size()-1).getBibliographicInformation();
		return information;
	}


	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<EsPrintManifestation> documents) {
		this.esPrintManifestations = documents;
	}

	/**
	 * instantiates a clone of the object
	 *
	 * @return a cloned object
	 */
	public Expression clone() {
	    Expression clone = new Expression(shelfmarkBase);
	    for (EsPrintManifestation document : esPrintManifestations)
	        clone.addManifestation(document);
	    return clone;
	}

}

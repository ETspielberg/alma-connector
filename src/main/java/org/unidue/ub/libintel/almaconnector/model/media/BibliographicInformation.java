package org.unidue.ub.libintel.almaconnector.model.media;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation object of the basic bibliographic information of one document
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class BibliographicInformation {

	private String titleId;

	@Field(analyzer = "keyword")
	private String isbn;

	@Field(analyzer = "keyword")
	private String doi;

	private List<String> authors;

	private String title;

	private String subtitle;

	private String publisher;

	private String place;

	@Field(type = FieldType.Integer)
	private String year;
	
	private String edition;
	
	private String series;

	@Field(type = FieldType.Integer)
	private int volume;
	
	private List<String> keywords;

	private String type;

	@Field(analyzer = "keyword")
	private String otherIdentifier;

	private String fullDescription;

	private String recKey;

	/**
	 * creates a new <code>Publication</code>-object
	 *
	 * @param titleId
	 *            the document number
	 * @param authors
	 *            the authors of the publication
	 * @param title
	 *            the title of the publication
	 * 
	 */
	public BibliographicInformation(String titleId, List<String> authors, String title) {
		this.titleId = titleId;
		this.authors = authors;
		this.title = title;
		type = "basic";
		isbn = "";
		subtitle = "";
		publisher = "";
		place = "";
		year = "";
		edition = "";
		series = "";
		recKey = "";
		volume = 0;
		otherIdentifier = "";
		fullDescription = "";
		keywords = new ArrayList<>();
	}

	public BibliographicInformation(String titleId, String recKey, List<String> authors, String title) {
		this.titleId = titleId;
		this.authors = authors;
		this.title = title;
		type = "basic";
		isbn = "";
		subtitle = "";
		publisher = "";
		place = "";
		year = "";
		edition = "";
		series = "";
		this.recKey = recKey;
		volume = 0;
		otherIdentifier = "";
		fullDescription = "";
		keywords = new ArrayList<>();
	}

	public BibliographicInformation() {
		titleId = "";
		isbn = "";
		authors = new ArrayList<>();
		title = "";
		subtitle = "";
		publisher = "";
		place = "";
		year = "";
		edition = "";
		recKey = "";
		series = "";
		volume = 0;
		keywords = new ArrayList<>();
		type = "empty";
		otherIdentifier = "";
		fullDescription  = "";
	}

	public String getRecKey() {
		return recKey;
	}

	public void setRecKey(String recKey) {
		this.recKey = recKey;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addAuthor(String author) {
		authors.add(author);
	}

	public String getOtherIdentifier() {
		return otherIdentifier;
	}

	public void setOtherIdentifier(String otherIdentifier) {
		this.otherIdentifier = otherIdentifier;
	}

	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	@Override
	public String toString() {
		String mab = "";
		if (authors != null) {
			for (int i = 0; i < authors.size(); i++) {
				if (i > 0)
					mab += ", ";
				mab += authors.get(i);
			}
		}
		if (!title.isEmpty())
			mab += ": " + title + ". ";
		if (!subtitle.isEmpty())
			mab += subtitle + ". ";
		if (!series.isEmpty())
			mab += "Erschienen in " + series + ".";
		if (volume != 0)
			mab += "Band " + volume;
		if (!edition.isEmpty())
			mab += edition + ". Ausgabe.";
		if (!publisher.isEmpty())
			mab += publisher + ", ";
		if (!place.isEmpty())
			mab += place + ", ";
		if (!year.equals(""))
			mab += year + ". ";
		return mab;
	}

}

package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Representation object of the basic bibliographic information of one document
 * 
 * @author Eike Spielberg
 * @version 1
 */
public class EsBibliographicInformation {

	private String titleId = "";

	private String isbn = "";

	private String doi = "";

	private List<String> authors = new ArrayList<>();

	private String title = "";

	private String subtitle = "";

	private String publisher = "";

	private String place = "";

	private int year = 0;
	
	private String edition = "";
	
	private String series = "";

	private int volume = 1;
	
	private Set<String> keywords = new HashSet<>();

	private String type = "";

	private String otherIdentifier = "";

	private String fullDescription = "";

	private String recKey = "";

	public EsBibliographicInformation() {
	}

	public EsBibliographicInformation(BibWithRecord bib) {
		this.title = bib.getTitle();
		this.isbn = bib.getIsbn();
		this.authors = new ArrayList<>();
		this.authors.add(bib.getAuthor());
		this.edition = bib.getCompleteEdition();
		this.publisher = bib.getPublisherConst();
		this.recKey = bib.getMmsId();
		XmlMapper xmlMapper = new XmlMapper();
		try {
			this.fullDescription = xmlMapper.writeValueAsString(bib.getRecord());
		} catch (JsonProcessingException e) {
			this.fullDescription = "";
		}
		String yearString = bib.getDateOfPublication();
		Pattern yearRegEx = Pattern.compile("[17,|18|19|20][0-9][0-9]");
		try {
			Integer.parseInt(yearString);
		} catch (Exception e) {
			try {
				String year = yearRegEx.matcher(yearString).group();
				if (year.length() == 4) {
					this.year = Integer.parseInt(year);
				} else {
					this.year = 0;
				}
			} catch (IllegalStateException ise) {
				this.year = 0;
			}
		}
		this.place = bib.getPlaceOfPublication();
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
		if (year != 0)
			mab += year + ". ";
		return mab;
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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
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

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOtherIdentifier() {
		return otherIdentifier;
	}

	public void setOtherIdentifier(String otherIdentifier) {
		this.otherIdentifier = otherIdentifier;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public String getRecKey() {
		return recKey;
	}

	public void setRecKey(String recKey) {
		this.recKey = recKey;
	}
}

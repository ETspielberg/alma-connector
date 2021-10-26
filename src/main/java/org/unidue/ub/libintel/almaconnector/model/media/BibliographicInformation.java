package org.unidue.ub.libintel.almaconnector.model.media;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.unidue.ub.alma.shared.bibs.BibWithRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation object of the basic bibliographic information of one document
 * 
 * @author Eike Spielberg
 * @version 1
 */
@Data
public class BibliographicInformation {

	private String titleId = "";

	@Field(analyzer = "keyword")
	private String isbn = "";

	@Field(analyzer = "keyword")
	private String doi = "";

	private List<String> authors = new ArrayList<>();

	private String title = "";

	private String subtitle = "";

	private String publisher = "";

	private String place = "";

	@Field(type = FieldType.Integer)
	private String year = "";
	
	private String edition = "";
	
	private String series = "";

	@Field(type = FieldType.Integer)
	private int volume = 1;
	
	private List<String> keywords = new ArrayList<>();

	private String type = "";

	@Field(analyzer = "keyword")
	private String otherIdentifier = "";

	private String fullDescription = "";

	private String recKey = "";


	public BibliographicInformation() {
	}

	public BibliographicInformation(BibWithRecord bib) {
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
		this.year = bib.getDateOfPublication();
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
		if (!year.equals(""))
			mab += year + ". ";
		return mab;
	}

}

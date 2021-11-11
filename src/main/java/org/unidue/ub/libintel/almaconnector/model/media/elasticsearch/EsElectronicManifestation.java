package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "electronic_manifestation_v1")
public class EsElectronicManifestation {

    private EsBibliographicInformation bibliographicInformation;

    private List<Chapter> chapters;

    public EsElectronicManifestation() {}

    public EsBibliographicInformation getBibliographicInformation() {
        return bibliographicInformation;
    }

    public void setBibliographicInformation(EsBibliographicInformation bibliographicInformation) {
        this.bibliographicInformation = bibliographicInformation;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}

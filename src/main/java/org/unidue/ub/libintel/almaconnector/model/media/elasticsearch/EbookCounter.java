package org.unidue.ub.libintel.almaconnector.model.media.elasticsearch;

public class EbookCounter extends Counter {

    private int year;

    private int month;

    private long htmlRequests = 0L;

    private long pdfRequests = 0L;

    private long totalRequests = 0L;

    private long epubRequest = 0L;

    public EbookCounter() {
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getHtmlRequests() {
        return htmlRequests;
    }

    public void setHtmlRequests(long htmlRequests) {
        this.htmlRequests = htmlRequests;
    }

    public long getPdfRequests() {
        return pdfRequests;
    }

    public void setPdfRequests(long pdfRequests) {
        this.pdfRequests = pdfRequests;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getEpubRequest() {
        return epubRequest;
    }

    public void setEpubRequest(long epubRequest) {
        this.epubRequest = epubRequest;
    }
}

package org.unidue.ub.libintel.almaconnector.clients.alma.analytics;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * retrieves Alma Analytics reports.
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
public class AlmaAnalyticsReportClient {

    private final static Logger log = LoggerFactory.getLogger(AlmaAnalyticsReportClient.class);

    @Value("${alma.api.key:1234}")
    private String almaAcqApiKey;

    /**
     * The general path for all Alma analytics reports
     */
    private final static String urlTemplate = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/analytics/reports?path=%s&apikey=%s";

    /**
     * retrieves the report, transforms it and maps it onto the given class
     * @param reportPath the path of the rport in the url
     * @param clazz the class, the List of reports shall be cast into
     * @param <T> the class type
     * @return returns a report of class <T>
     * @throws IOException thrown if the transformation results in errors.
     */
    public <T> T getReport(String reportPath, Class<T> clazz) throws IOException {
        String url = String.format(urlTemplate, reportPath, almaAcqApiKey);
        log.debug("querying url: " + url);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        log.debug("queried alma api with response: " + response);
        // XSL file written by Frank Lützenkirchen
        InputStream xslFile = new ClassPathResource("/xslt/analytics2xml.xsl").getInputStream();
        String transformed = transformXmlDocument(response, xslFile);
        log.debug("converted response into string: " + transformed);
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(transformed, clazz);
    }


    /**
     * helper function for the xsl transformation
     * @param inputXmlString the String to be transformed
     * @param xsltFile the filename relativ to the resources folder
     * @return the transformed xml as string
     */
    private String transformXmlDocument(String inputXmlString,
                                              InputStream xsltFile) {

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(xsltFile);

        StreamSource text = new StreamSource(new StringReader(inputXmlString));
        StringWriter writer = new StringWriter();
        StreamResult textOP = new StreamResult(writer);

        try {
            Transformer transformer = factory.newTransformer(xslt);
            transformer.setParameter("apikey", this.almaAcqApiKey);
            transformer.transform(text, textOP);
        } catch (TransformerException e) {
            log.error("could not transform analytics report", e);
        }
        return writer.toString();
    }


}

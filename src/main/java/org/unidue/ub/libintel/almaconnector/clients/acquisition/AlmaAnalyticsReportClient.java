package org.unidue.ub.libintel.almaconnector.clients.acquisition;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.unidue.ub.libintel.almaconnector.model.ItemLoanedReport;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Service
public class AlmaAnalyticsReportClient {

    @Value("${alma.api.key}")
    private String almaAcqApiKey;

    private Logger log = LoggerFactory.getLogger(AlmaAnalyticsReportClient.class);

    public ItemLoanedReport getReport() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api-eu.hosted.exlibrisgroup.com/almaws/v1/analytics/reports?path=%2Fshared%2FUniversität+Duisburg-Essen+49HBZ_UDE%2FExemplaregestrigeAusleihe&apikey=" + almaAcqApiKey;
        log.info(url);
        String response = restTemplate.getForObject("https://api-eu.hosted.exlibrisgroup.com/almaws/v1/analytics/reports?path=%2Fshared%2FUniversität+Duisburg-Essen+49HBZ_UDE%2FExemplaregestrigeAusleihe&apikey=" + almaAcqApiKey, String.class);
        log.info(response);
        File xslFile = new ClassPathResource("/xsl/OverdueReport.xsl").getFile();
        String transformed = transformXmlDocument(response, xslFile);
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(transformed, ItemLoanedReport.class);
    }

    public static String transformXmlDocument(String inputXmlString,
                                              File xsltFile) {

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xslt = new StreamSource(xsltFile);

        StreamSource text = new StreamSource(new StringReader(inputXmlString));
        StringWriter writer = new StringWriter();
        StreamResult textOP = new StreamResult(writer);

        try {
            Transformer transformer = factory.newTransformer(xslt);
            transformer.transform(text, textOP);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


}

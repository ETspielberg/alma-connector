package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.unidue.ub.alma.shared.conf.Job;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class XmlReaderService {

    @Value("${libintel.data.dir")
    private String libintelDataDir;

    public Job readJobParameters(String job) {
        try {
            String pathToXml = String.format("%s/jobs/%s.xml", libintelDataDir, job);
            InputStream xmlFile = new ClassPathResource(String.format(pathToXml,job)).getInputStream();
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlFile, Job.class);
        } catch (IOException e) {
            log.error("could not read in job parameters for Job " + job, e);
            return new Job();
        }
    }
}

package org.unidue.ub.libintel.almaconnector.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.JobParametersFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * reading xml files, e.g. the job parameters for starting jobs
 */
@Service
@Slf4j
public class XmlReaderService {

    @Value("${libintel.data.dir}")
    private String libintelDataDir;

    /**
     * reads the file from the jobs directory
     * @param jobId the ID of the job the parameters shall be loaded
     * @return a <code>JobParametersFile</code> object
     */
    public JobParametersFile readJobParameters(String jobId) {
        try {
            String pathToXml = String.format("%s/jobs/%s.xml", libintelDataDir, jobId);
            File xmlFile = new File(pathToXml);
            InputStream xmlFileStream = new FileInputStream(xmlFile);
            XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.readValue(xmlFileStream, JobParametersFile.class);
        } catch (IOException e) {
            log.error("could not read in job parameters for Job " + jobId, e);
            return new JobParametersFile();
        }
    }
}

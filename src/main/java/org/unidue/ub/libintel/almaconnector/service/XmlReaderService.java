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
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class XmlReaderService {

    @Value("${libintel.data.dir}")
    private String libintelDataDir;

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

    public String readJobParamtersAsString(String job) {
        try {
            String pathToXml = String.format("%s/jobs/%s.xml", libintelDataDir, job);
            return Files.readString(Paths.get(pathToXml));
        } catch (IOException e) {
            log.error("could not read in job parameters for Job " + job, e);
            return "";
        }
    }
}

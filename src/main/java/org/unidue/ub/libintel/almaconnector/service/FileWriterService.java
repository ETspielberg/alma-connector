package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.SapData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.unidue.ub.libintel.almaconnector.service.LocalizationService.generateComment;

@Service
public class FileWriterService {

    private final Logger log = LoggerFactory.getLogger(FileWriterService.class);

    private final String dataDir;

    private final String file;

    /**
     * constructor based autowiring with the data directory. Creates the folder if it does not exist.
     * @param dataDir the config property ub.statistics.data.dir
     */
    // put in @Value annotation in constructor to make sure, the value is initiated before the bean is created.
    FileWriterService(@Value("${ub.statistics.data.dir}") String dataDir) {
        this.dataDir = dataDir;
        this.file = this.dataDir + "/sapData/";
        File folder = new File(this.file);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                log.warn("could not create data directory");
        }
    }

    /**
     * writes the list of sapData to the two output files.
     * @param sapDataList a list of sapData
     * @return the number of sap data which could not be written
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public int writeLines(List<SapData> sapDataList, String currentDate) {
        int failures = 0;
        String checkFilename = "Druck_sap-" + currentDate + ".txt";
        String sapFilename = "sap-" + currentDate + ".txt";
        initializeFiles(currentDate, checkFilename, sapFilename);
        for (SapData sapData: sapDataList) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + checkFilename, true))) {
                addLine(bw, sapData.toFixedLengthLine());
            } catch(IOException ex) {
                failures++;
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + sapFilename, true))) {
                addLine(bw, generateComment(sapData).toCsv());
            } catch(IOException ex) {
                failures++;
                log.warn("could not write line: " + sapData.toCsv());
            }
        }
        return failures;
    }

    /**
     * adds a line to the output file
     * @param bw the buffered writer for the file
     * @param line the line to be added
     * @throws IOException thrown if problems writing to the file occur
     */
    private void addLine(BufferedWriter bw, String line) throws IOException {
        bw.write(line);
        bw.newLine();
        bw.flush();
    }

    /**
     * @param currentDate the current date
     * @param checkFilename the filename of the check/printed version
     * @param sapFilename the filename of the SAP import file
     */
    private void initializeFiles(String currentDate, String checkFilename, String sapFilename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + checkFilename, false))) {
            addLine(bw, "Kontrollausdruck der SAP-Datei, Bearbeitungsdatum: " + currentDate);
        } catch(IOException ioe) {
            log.warn("could not create empty check file at " + currentDate, ioe);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + sapFilename, false))) {
            bw.write("");
            bw.flush();
        } catch(IOException ioe) {
            log.warn("could not create empty sap file at " + currentDate, ioe);
        }
    }
}

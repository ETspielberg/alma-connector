package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.run.AlmaExportRun;
import org.unidue.ub.libintel.almaconnector.model.SapData;
import org.unidue.ub.libintel.almaconnector.repository.AlmaExportRunRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.unidue.ub.libintel.almaconnector.Utils.dateformat;
import static org.unidue.ub.libintel.almaconnector.service.LocalizationService.generateComment;

@Service
public class FileWriterService {

    private final Logger log = LoggerFactory.getLogger(FileWriterService.class);

    private final String file;

    private final AlmaExportRunRepository almaExportRunRepository;
    /**
     * constructor based autowiring with the data directory. Creates the folder if it does not exist.
     * @param dataDir the config property ub.statistics.data.dir
     */
    // put in @Value annotation in constructor to make sure, the value is initiated before the bean is created.
    FileWriterService(@Value("${ub.statistics.data.dir}") String dataDir, AlmaExportRunRepository almaExportRunRepository) {
        this.file = dataDir + "/sapData/";
        this.almaExportRunRepository = almaExportRunRepository;
        File folder = new File(this.file);
        if (!folder.exists()) {
            if (!folder.mkdirs())
                log.warn("could not create data directory");
        }
    }

    /**
     * writes the SAP data contained in an AlmaExportRun object as files to disk
     * @param almaExportRun the AlmaExportRun object holding a list of SAP data
     * @return the AlmaExportRun object updated with the files created and the number of failed entries to be written
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public AlmaExportRun writeAlmaExport(AlmaExportRun almaExportRun) {
        String checkFilename;
        String sapFilename;
        String foreignFilename;
        String currentDate = dateformat.format(new Date());
        if (almaExportRun.isDateSpecific()) {
            // initialize failure counter, print file and csv-file
            checkFilename = "Druck_sap-" + dateformat.format(almaExportRun.getDesiredDate()) + ".txt";
            sapFilename = "sap-" + dateformat.format(almaExportRun.getDesiredDate()) + ".txt";
            foreignFilename = "foreign-sap-" + dateformat.format(almaExportRun.getDesiredDate()) + ".txt";
        } else {
            checkFilename = "Druck_sap.txt";
            sapFilename = "sap.txt";
            foreignFilename = "foreign-sap.txt";
        }
        initializeFiles(currentDate, checkFilename, sapFilename, foreignFilename);

        for (SapData sapData: almaExportRun.getSapData()) {
            try {
                addLineToFile(checkFilename, sapData.toFixedLengthLine());
            } catch (IOException ex) {
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
            try {
                if (("H9".equals(sapData.costType) || "H8".equals(sapData.costType)) && "EUR".equals(sapData.currency))
                    addLineToFile(sapFilename, generateComment(sapData).toCsv());
                else
                    addLineToFile(foreignFilename, generateComment(sapData).toCsv());
                almaExportRun.increaseSuccessfullSapData();
            } catch (IOException ex) {
                almaExportRun.increaseMissedSapData();
                almaExportRun.addMissedSapData(sapData);
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
        }
        this.almaExportRunRepository.save(almaExportRun);
        return almaExportRun;
    }

    private void addLineToFile(String filename, String line) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + filename, true));
        bw.write(line);
        bw.newLine();
        bw.flush();
        bw.close();
    }

    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public List<String> getFiles(@Value("${ub.statistics.data.dir}") String dataDir) {
        Path rootLocation = Paths.get(dataDir);
        try {
            return Files.walk(rootLocation, 1)
                    .filter(path -> !path.equals(rootLocation))
                    .map(rootLocation::relativize)
                    .map(path -> path.getFileName().toString())
                    .filter(filename -> !filename.startsWith("Druck"))
                    .map(filename -> filename.replace("sap-",""))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            log.error("failed to read stored files", ioe);
            return null;
        }
    }

    /**
     * writes the list of sapData to the two output files.
     * @param sapDataList a list of sapData
     * @return the number of sap data which could not be written
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public int writeLines(List<SapData> sapDataList, String currentDate) {
        // initialize failure counter, print file and csv-file
        int failures = 0;
        String checkFilename = "Druck_sap-" + currentDate + ".txt";
        String sapFilename = "sap-" + currentDate + ".txt";
        String foreignFilename = "foreign-sap-" + currentDate + ".txt";
        initializeFiles(currentDate, checkFilename, sapFilename, foreignFilename);
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
     * initialize files and deletes the files if present.
     * @param currentDate the current date
     * @param checkFilename the filename of the check/printed version
     * @param sapFilename the filename of the SAP import file
     */
    private void initializeFiles(String currentDate, String checkFilename, String sapFilename, String foreignFilename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + checkFilename, false))) {
            addLine(bw, "Kontrollausdruck der SAP-Datei, Bearbeitungsdatum: " + currentDate);
        } catch(IOException ioe) {
            log.warn("could not create empty check file at " + currentDate, ioe);
        }
        initializeSapFiles(sapFilename);
        initializeSapFiles(foreignFilename);
    }

    private void initializeSapFiles(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + filename, false))) {
            bw.write("");
            bw.flush();
        } catch(IOException ioe) {
            log.warn("could not create empty sap file.", ioe);
        }
    }

    /**
     * loads the sap files from  disk
     * @param date the date for which the file shall be obtained
     * @param type the type of the file to be obtained (HomeCurrency or ForeignCurrency)
     * @return the file as Resource object
     * @throws FileNotFoundException thrown if the file could not be loaded
     */
    @Secured({ "ROLE_SYSTEM", "ROLE_SAP" })
    public Resource loadFiles(String date, String type) throws FileNotFoundException {
        String filename;
        if ("all".equals(date)) {
            if ("sap".equals(type))
                filename = "sap.txt";
            else
                filename = "foreign-sap.txt";
        } else {
            if ("sap".equals(type))
                filename = "sap-" + date + ".txt";
            else
                filename = "foreign-sap-" + date + ".txt";
        }
        Path file =Paths.get(this.file + filename);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error("could not read file: " + filename, e);
            return null;
        }
    }
}

package org.unidue.ub.libintel.almaconnector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.almaconnector.model.SapData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class FileWriterService {

    private final Logger log = LoggerFactory.getLogger(FileWriterService.class);

    @Value("${ub.statistics.data.dir}")
    private String dataDir;

    private final String file;

    FileWriterService() {
        this.file = dataDir + "/sapData/";
        File folder = new File(this.file);
        if (!folder.exists())
            folder.mkdirs();
    }

    public int writeLines(List<SapData> sapDataList) {
        int failures = 0;
        for (SapData sapData: sapDataList) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + "fixed_width.txt", true))) {
                String line = sapData.toFixedLengthLine();
                bw.write(line);
                bw.newLine();
                bw.flush();
            } catch(IOException ex) {
                failures++;
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.file + "data.csv", true))) {
                String line = sapData.toCsv();
                bw.write(line);
                bw.newLine();
                bw.flush();
            } catch(IOException ex) {
                failures++;
                log.warn("could not write line: " + sapData.toFixedLengthLine());
            }
        }
        return failures;
    }
}

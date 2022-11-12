package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class LogfileFilesList {

    private final String logDirectoryName;
    private final String workDirectoryName;

    private final ArrayList<LogfileDescription> logfileDescriptionList;

    public LogfileFilesList(String logDirectoryName, String workDirectoryName) throws IOException {
        this.logDirectoryName = logDirectoryName;
        this.workDirectoryName = workDirectoryName;
        logfileDescriptionList = new ArrayList<>();

        init();
    }

    private void init() throws IOException {
        if (logDirectoryName.isEmpty()) {
            throw (new IOException("Log directory name should not be empty"));
        }
        if (workDirectoryName.isEmpty()) {
            throw (new IOException("Work directory name should not be empty"));
        }

        Path logDirectoryPath = Paths.get(logDirectoryName);
        try (Stream<Path> pathStream = Files.find(logDirectoryPath, 1, (p, attr) -> p.getFileName().toString().endsWith(".lgp"))) {
            pathStream.forEach(this::appendLogfile);
        } catch (IOException e) {
            log.debug("Error listing log files directory:", e);
        }
    }

    private void appendLogfile(Path filePath) {
        Path descriptionFilePath = Paths.get(workDirectoryName, filePath.getFileName().toString() + ".rpf");
        LogfileDescription logfileDescription;
        if (!Files.exists(descriptionFilePath)) {
            logfileDescription = new LogfileDescription(filePath, workDirectoryName);
        }
        else {
            try (BufferedReader bufferedReader = Files.newBufferedReader(descriptionFilePath, StandardCharsets.UTF_8)) {
                ObjectMapper objectMapper = new ObjectMapper();
                logfileDescription = objectMapper.readValue(bufferedReader, LogfileDescription.class);
                logfileDescription.setFilePath(filePath);
                logfileDescription.setWorkDirectoryName(workDirectoryName);
            } catch (IOException e) {
                log.debug("Error loading logfile description file ", e);
                logfileDescription = new LogfileDescription(filePath, workDirectoryName);
            }
        }

        logfileDescriptionList.add(logfileDescription);
    }

    public List<LogfileDescription> getFileDescriptionListToLoad() {
        return logfileDescriptionList.stream().filter(LogfileDescription::isSubjectToRead).collect(Collectors.toList());
    }

}

package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

@Slf4j
@JsonAutoDetect
public class LogfileDescription {
    @Getter @Setter
    @JsonIgnore
    private Path filePath;
    @Getter
    private Long lastModified;
    @Getter
    private String lastModifiedDate;
    @Getter
    private int itemsProcessed;

    @Getter @Setter
    @JsonIgnore
    private String workDirectoryName;

    public LogfileDescription(Path filePath, String workDirectoryName) {
        this.filePath = filePath;
        this.workDirectoryName = workDirectoryName;
    }

    public LogfileDescription() {

    }

    public void save() {
        if (lastModified == null) {
            return;
        }

        Path descriptionFilePath = Paths.get(workDirectoryName, filePath.getFileName().toString() + ".rpf");

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(descriptionFilePath)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(bufferedWriter, this);
        } catch (IOException e) {
            log.debug("Error saving logfile description file: ", e);
        }
    }

    @JsonIgnore
    public boolean isSubjectToRead() {
        if (lastModified == null ) {
            return true;
        }

        BasicFileAttributes basicFileAttributes = null;
        try {
            basicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            return lastModified < basicFileAttributes.lastModifiedTime().toMillis();
        } catch (IOException e) {
            log.debug("Error reading logfile attributes: ", e);
            return false;
        }
    }

    public void readDone(int itemsProcessed) {
        this.itemsProcessed += itemsProcessed;

        BasicFileAttributes basicFileAttributes = null;
        try {
            basicFileAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            lastModified = basicFileAttributes.lastModifiedTime().toMillis();
            lastModifiedDate = new Date(lastModified).toString();
        } catch (IOException e) {
            log.debug("Error reading logfile attributes: ", e);
            e.printStackTrace();
        }
    }

}

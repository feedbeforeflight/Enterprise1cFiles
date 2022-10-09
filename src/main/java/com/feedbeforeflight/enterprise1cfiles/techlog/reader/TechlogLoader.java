package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TechlogLoader {

    private final TechlogDescription techlogDescription;
    private final TechlogItemWriter writer;

    public TechlogLoader(TechlogDescription techlogDescription, TechlogItemWriter writer) {
        this.techlogDescription = techlogDescription;
        this.writer = writer;
    }

    public void load() {
        try {
            techlogDescription.refreshFiles();
        } catch (IOException e) {
            log.error("Error refreshing files", e);
            return;
        }

        for (DirectoryDescriptionProcessor directoryDescription : techlogDescription.getDirectories().values()) {
            log.info("reading directory for process {} with pid {}", directoryDescription.getProcessType(), directoryDescription.getProcessId());

            for (TechlogFileDescription fileDescription : directoryDescription.getFiles().values()) {
                new TechlogFileLoader(writer, fileDescription).loadFile();
            }
        }
    }

}

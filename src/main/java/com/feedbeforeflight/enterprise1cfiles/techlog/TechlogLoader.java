package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogProcessDirectoryProcessor;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogFileDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
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

        for (TechlogProcessDirectoryProcessor directoryProcessor : techlogDescription.getDirectories().values()) {
            log.info("reading directory for process {} with pid {}", directoryProcessor.getProcessType(), directoryProcessor.getProcessId());

            for (TechlogFileDescription fileDescription : directoryProcessor.getFiles().values()) {
                new TechlogFileLoader(writer, fileDescription).loadFile();
            }
        }
    }

}

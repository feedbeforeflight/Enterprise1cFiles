package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogDirectoryDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogFileDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            techlogDescription.readFileDescriptions();
        } catch (IOException e) {
            log.error("Error refreshing files", e);
            return;
        }

        for (TechlogDirectoryDescription directoryDescription : techlogDescription.directories()) {
            log.debug("reading directory for process {} with pid {}", directoryDescription.getProcessType(), directoryDescription.getProcessId());

            for (TechlogFileDescription fileDescription : directoryDescription.files()) {
                TechlogFileLoader.load(writer, fileDescription);
            }
        }
    }

}

package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogDirectory;
import com.feedbeforeflight.enterprise1cfiles.techlog.source.Techlog;
import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TechlogLoader {

    private final Techlog techlog;
    private final TechlogItemWriter writer;

    public TechlogLoader(Techlog techlog, TechlogItemWriter writer) {
        this.techlog = techlog;
        this.writer = writer;
    }

    public void load() {
        try {
            techlog.refresh();
        } catch (IOException e) {
            log.error("Error refreshing files", e);
            return;
        }

        for (TechlogDirectory techlogDirectory : techlog.directories()) {
            log.debug("reading directory for process {} with pid {}", techlogDirectory.getProcessType(), techlogDirectory.getProcessId());

            for (TechlogFile techlogFile : techlogDirectory.files()) {
                TechlogFileLoader.load(writer, techlogFile);
            }
        }
    }

}

package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogFileDescription;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogEventFactory;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileFieldTokenizer;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileReader;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogItemProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TechlogFileLoader {

    private final TechlogItemWriter writer;
    private final TechlogFileDescription description;
    private EnumMap<TechlogEventType, Integer> summary = new EnumMap<TechlogEventType, Integer>(TechlogEventType.class);
    private int skipped = 0;

    public TechlogFileLoader(TechlogItemWriter writer, TechlogFileDescription description) {
        this.writer = writer;
        this.description = description;
    }

    public void loadFile() {

        TechlogEventFactory factory = new TechlogEventFactory("erp-01-0x", "erp-01-01");
        AbstractTechlogEvent event = null;
        try (TechlogFileReader reader = new TechlogFileReader(description)) {
            reader.openFile();
            log.info("- reading fileID {} timestamp {}", description.getId(), description.getTimestamp());

//            skipped = reader.getSkippedLines();
            skipped = 0;

            Deque<String> lines = reader.readItemLines();
            while(!lines.isEmpty()) {
                List<String> tokens = TechlogFileFieldTokenizer.readEventTokens(lines);
                Map<String, String> parameters = TechlogItemProcessor.process(tokens, description, reader.getLineNumber());
                event = factory.createEvent(parameters, event);
                if (event != null && (description.getLastLoadedEventTimestamp() == null ||
                        event.getTimestamp().isAfter(description.getLastLoadedEventTimestamp()))) {
                    writer.writeItem(event);

                    summary.compute(event.getType(), (k, v) -> (v == null) ? 1 : v + 1);
                }
                else {skipped++;}

                lines = reader.readItemLines();
            }
        } catch (IOException e) {
            log.error("Something gone wrong while reading file " + description.getId(), e);
        }

        log.info("-- skipped {} events", skipped);
        log.info("-- loaded:");
        for (TechlogEventType eventType : summary.keySet()) {
            log.info("--- {} - {} events", eventType, summary.get(eventType));
        }
        if (summary.isEmpty()) {
            log.info("--- nothing");
        }

        writer.loadFinished(description.getId());  // create event for file loaded (fire only if something read)
    }

}

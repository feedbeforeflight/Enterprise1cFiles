package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogEventFactory;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileFieldTokenizer;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileReader;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogItemProcessor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TechlogFileLoader {

    public static EnumMap<TechlogEventType, Integer> load(TechlogItemWriter writer, TechlogFile techlogFile) {
        int skipped = 0;
        EnumMap<TechlogEventType, Integer> summary = new EnumMap<>(TechlogEventType.class);

        TechlogEventFactory factory = new TechlogEventFactory();
        AbstractTechlogEvent event = null;
        Instant lastRead = Instant.now();
        try (TechlogFileReader reader = new TechlogFileReader(techlogFile)) {
            reader.openFile();
            lastRead = Instant.now();
            log.debug("- reading fileID {} timestamp {}", techlogFile.getId(), techlogFile.getTimestamp());

            skipped = techlogFile.getLinesRead();
            if (skipped > 0) {
                reader.skipToLine(skipped);
                log.debug("- already loaded {}", techlogFile.getLinesRead());
            };

            Deque<String> lines = reader.readItemLines();
            while(!lines.isEmpty()) {
                List<String> tokens = TechlogFileFieldTokenizer.readEventTokens(lines);
                Map<String, String> parameters = TechlogItemProcessor.process(tokens, techlogFile, reader.getLineNumber());
                event = factory.createEvent(parameters, event);
                if (event != null && (techlogFile.getLastLoadedEventTimestamp() == null ||
                        event.getTimestamp().isAfter(techlogFile.getLastLoadedEventTimestamp()))) {
                    writer.writeItem(event);

                    summary.compute(event.getType(), (k, v) -> (v == null) ? 1 : v + 1);
                }
                else {skipped++;}
                techlogFile.setLinesRead(reader.getLineNumber() - (reader.EOF() ? 0 : 1));

                lines = reader.readItemLines();
            }
            reader.closeFile();
        } catch (Exception e) {
            log.error("Something gone wrong while reading file " + techlogFile.getId(), e);
        }

        log.debug("-- skipped {} events", skipped);
        log.debug("-- loaded:");
        for (TechlogEventType eventType : summary.keySet()) {
            log.debug("--- {} - {} events", eventType, summary.get(eventType));
        }
        if (summary.isEmpty()) {
            log.debug("--- nothing");
        } else {
            writer.loadFinished(techlogFile.getId());  // let writer know, that we've done
        }
        techlogFile.updateLastRead(lastRead);

        return summary;
    }

}

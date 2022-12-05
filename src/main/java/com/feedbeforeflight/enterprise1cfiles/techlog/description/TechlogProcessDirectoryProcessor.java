package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TechlogProcessDirectoryProcessor {

    @Getter
    private final Path path;
    @Getter
    private final TechlogProcessType processType;
    @Getter
    private final int processId;
    @Getter
    private final String groupName;
    @Getter
    private final String serverName;
    @Getter
    private final Map<String, TechlogFileDescription> files;
    private final TechlogItemWriter writer;
    private final Integer fileLastProgressRequestPacketSize = 10;


    public TechlogProcessDirectoryProcessor(Path path, String groupName, String serverName, TechlogItemWriter writer) {
        this.path = path;
        this.serverName = serverName;
        this.groupName = groupName;
        this.writer = writer;

        String directoryName = path.getFileName().toString();
        String[] tags = directoryName.split("_");

        this.processType = TechlogProcessType.getByName(tags[0]);
        this.processId = Integer.parseInt(tags[1]);

        files = new HashMap<>();
    }

    public void refreshFiles() {
        try (Stream<Path> pathStream = Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".log"))) {
            pathStream.forEach(this::appendLogfile);
        } catch (IOException e) {
            log.debug("Error listing log files directory:", e);
        }

        checkEventsLoadedAlready();
    }

    private void checkEventsLoadedAlready() {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<TechlogFileDescription>> chunkedDescriptions =
                files.values().stream().collect(Collectors
                        .groupingBy(x -> index.getAndIncrement() / fileLastProgressRequestPacketSize));

        chunkedDescriptions.forEach((key, value) -> {
            Map<String, Instant> lastProgressBatchResult = writer.getLastProgressBatch(
                    value.stream().map(TechlogFileDescription::getId).collect(Collectors.toList()));
            lastProgressBatchResult.forEach((s, instant) -> files.get(s).setLastLoadedEventTimestamp(instant));
        });
    }

    private void appendLogfile(Path filePath) {
        try {
            long fileSize = Files.size(filePath);
            if (fileSize <= 3) { return; }
        } catch (IOException e) {
            return;
        }

        //todo: check for file last modified time here - no need to load old files

        TechlogFileDescription fileDescription = new TechlogFileDescription(filePath, processType, processId, groupName, serverName, writer);
        files.put(fileDescription.getId(), fileDescription);
    }
}

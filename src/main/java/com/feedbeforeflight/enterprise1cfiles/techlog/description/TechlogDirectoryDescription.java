package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class TechlogDirectoryDescription {

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
    @Getter @Setter
    private boolean directoryDeleted;

    public TechlogDirectoryDescription(Path path, String groupName, String serverName, TechlogItemWriter writer) {
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

    public void readLogfileDescriptions() {
        files.values().forEach(techlogFileDescription -> techlogFileDescription.setFileDeleted(true));
        List<TechlogFileDescription> newlyDiscoveredDescriptions = new ArrayList<>();

        try (Stream<Path> pathStream = Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".log"))) {
            pathStream.forEach(p -> readLogfileDescription(p, newlyDiscoveredDescriptions));
        } catch (IOException e) {
            log.debug("Error listing log files directory:", e);
            return;
        }

//        files.entrySet().stream().filter(entry -> entry.getValue().isFileDeleted()).
//                forEach(entry -> files.remove(entry.getKey()));
        files.entrySet().stream().filter(entry -> entry.getValue().isFileDeleted()).toList().
                forEach(entry -> files.remove(entry.getKey()));

        checkEventsLoadedAlreadyByPortions(newlyDiscoveredDescriptions);
    }

    private void checkEventsLoadedAlreadyByPortions(List<TechlogFileDescription> newlyDiscoveredFiles) {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<TechlogFileDescription>> descriptionPortions =
                newlyDiscoveredFiles.stream().collect(Collectors
                        .groupingBy(x -> index.getAndIncrement() / fileLastProgressRequestPacketSize));

        descriptionPortions.forEach((key, value) -> {
            Map<String, Instant> lastProgressBatchResult = writer.getLastProgressBatch(
                    value.stream().map(TechlogFileDescription::getId).collect(Collectors.toList()));
            lastProgressBatchResult.forEach((s, instant) -> files.get(s).setLastLoadedEventTimestamp(instant));
        });
    }

    private void readLogfileDescription(Path filePath, List<TechlogFileDescription> newlyDiscoveredDescriptions) {
        try {
            long fileSize = Files.size(filePath);
            if (fileSize <= 3) { return; } // there is BOM only - not interested
        } catch (IOException e) {
            return;
        }

        String fileId = TechlogFileDescription.createFileId(filePath, processType, processId);
        files.computeIfAbsent(fileId, s -> {
            TechlogFileDescription newTechlogFileDescription = new TechlogFileDescription(filePath, processType, processId, groupName, serverName, writer);
            newlyDiscoveredDescriptions.add(newTechlogFileDescription);
            return newTechlogFileDescription;
        }).setFileDeleted(false);
    }

}

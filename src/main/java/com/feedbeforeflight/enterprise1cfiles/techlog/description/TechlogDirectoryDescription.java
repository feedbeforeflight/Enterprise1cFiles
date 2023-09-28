package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.AccessLevel;
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
    private final Map<String, TechlogFileDescription> fileDescriptions;
    private final TechlogItemWriter writer;
    private final Integer fileLastProgressRequestPacketSize = 10;
    @Getter @Setter(AccessLevel.PACKAGE)
    protected boolean directoryDeleted;

    public TechlogDirectoryDescription(Path path, String groupName, String serverName, TechlogItemWriter writer) {
        this.path = path;
        this.serverName = serverName;
        this.groupName = groupName;
        this.writer = writer;

        String directoryName = path.getFileName().toString();
        String[] tags = directoryName.split("_");
        if (tags[0].isEmpty()) {
            throw new IllegalArgumentException("Empty type for folder " + path);
        }

        this.processType = TechlogProcessType.getByName(tags[0]);
        this.processId = Integer.parseInt(tags[1]);

        fileDescriptions = new HashMap<>();
    }

    public void readLogfileDescriptions() {
        fileDescriptions.values().forEach(techlogFileDescription -> techlogFileDescription.setFileDeleted(true));
        List<TechlogFileDescription> newlyDiscoveredDescriptions = new ArrayList<>();

        try (Stream<Path> pathStream = Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".log"))) {
            pathStream.forEach(p -> readLogfileDescription(p, newlyDiscoveredDescriptions));
        } catch (IOException e) {
            log.error("Error listing log files directory:", e);
            return;
        }

//        files.entrySet().stream().filter(entry -> entry.getValue().isFileDeleted()).
//                forEach(entry -> files.remove(entry.getKey()));
        fileDescriptions.entrySet().stream().filter(entry -> entry.getValue().isFileDeleted()).toList().
                forEach(entry -> fileDescriptions.remove(entry.getKey()));

        checkEventsLoadedAlreadyByPortions(newlyDiscoveredDescriptions);

        log.debug("Readed {} descriptions for directory {}", fileDescriptions.size(), path);
    }

    private void checkEventsLoadedAlreadyByPortions(List<TechlogFileDescription> newlyDiscoveredFiles) {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<TechlogFileDescription>> descriptionPortions =
                newlyDiscoveredFiles.stream().collect(Collectors
                        .groupingBy(x -> index.getAndIncrement() / fileLastProgressRequestPacketSize));

        descriptionPortions.forEach((key, value) -> {
            Map<String, Instant> lastProgressBatchResult = writer.getLastProgressBatch(
                    value.stream().map(TechlogFileDescription::getId).collect(Collectors.toList()));
            lastProgressBatchResult.forEach((s, instant) -> fileDescriptions.get(s).setLastLoadedEventTimestamp(instant));
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
        fileDescriptions.computeIfAbsent(fileId, s -> {
            TechlogFileDescription newTechlogFileDescription = new TechlogFileDescription(filePath, processType, processId, groupName, serverName);
            newlyDiscoveredDescriptions.add(newTechlogFileDescription);
            return newTechlogFileDescription;
        }).setFileDeleted(false);

        log.debug("Read non-empty file description {}", filePath);
    }

    public int size() {
        return fileDescriptions.size();
    }

    public TechlogFileDescription getFileDescription(String id) {
        return fileDescriptions.get(id);
    }

    public List<TechlogFileDescription> files() {
        return new ArrayList<>(fileDescriptions.values());
    }

}

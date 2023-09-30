package com.feedbeforeflight.enterprise1cfiles.techlog.source;

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
public class TechlogDirectory {

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
    private final Map<String, TechlogFile> files;
    private final TechlogItemWriter writer;
    private final Integer fileLastProgressRequestPacketSize = 10;
    @Getter @Setter(AccessLevel.PACKAGE)
    protected boolean deleted;

    public TechlogDirectory(Path path, String groupName, String serverName, TechlogItemWriter writer) {
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

        files = new HashMap<>();
    }

    public void refreshLogFiles() {
        files.values().forEach(techlogFile -> techlogFile.setDeleted(true));
        List<TechlogFile> newlyDiscoveredFiles = new ArrayList<>();

        try (Stream<Path> pathStream = Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".log"))) {
            pathStream.forEach(p -> addNonEmptyLogfile(p, newlyDiscoveredFiles));
        } catch (IOException e) {
            log.error("Error listing log files directory:", e);
            return;
        }

        // дополнительное преобразование toList чтобы не получить ConcurrentModificationException
        files.entrySet().stream().filter(entry -> entry.getValue().isDeleted()).toList().
                forEach(entry -> files.remove(entry.getKey()));

        getEventTimestampsLoadedAlreadyByPortions(newlyDiscoveredFiles);

        log.debug("Readed {} descriptions for directory {}", files.size(), path);
    }

    private void getEventTimestampsLoadedAlreadyByPortions(List<TechlogFile> newlyDiscoveredFiles) {
        AtomicInteger index = new AtomicInteger(0);
        Map<Integer, List<TechlogFile>> techlogfilePortions =
                newlyDiscoveredFiles.stream().collect(Collectors
                        .groupingBy(x -> index.getAndIncrement() / fileLastProgressRequestPacketSize));

        techlogfilePortions.forEach((key, value) -> {
            Map<String, Instant> lastProgressBatchResult = writer.getLastProgressBatch(
                    value.stream().map(TechlogFile::getId).collect(Collectors.toList()));
            lastProgressBatchResult.forEach((s, instant) -> files.get(s).setLastLoadedEventTimestamp(instant));
        });
    }

    private void addNonEmptyLogfile(Path filePath, List<TechlogFile> newlyDiscoveredFiles) {
        try {
            long fileSize = Files.size(filePath);
            if (fileSize <= 3) { return; } // there is BOM only - not interested
        } catch (IOException e) {
            return;
        }

        String fileId = TechlogFile.createFileId(filePath, processType, processId);
        files.computeIfAbsent(fileId, s -> {
            TechlogFile newTechlogFile = new TechlogFile(filePath, processType, processId, groupName, serverName);
            newlyDiscoveredFiles.add(newTechlogFile);
            return newTechlogFile;
        }).setDeleted(false);

        log.debug("Read non-empty file description {}", filePath);
    }

    public int size() {
        return files.size();
    }

    public TechlogFile getFileById(String id) {
        return files.get(id);
    }

    public List<TechlogFile> files() {
        return new ArrayList<>(files.values());
    }

}

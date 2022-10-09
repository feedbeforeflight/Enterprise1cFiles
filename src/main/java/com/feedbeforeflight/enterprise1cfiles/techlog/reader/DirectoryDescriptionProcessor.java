package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class DirectoryDescriptionProcessor {

    @Getter
    private final Path path;
    @Getter
    private final TechlogProcessType processType;
    @Getter
    private final int processId;
    @Getter
    private final String serverName;
    @Getter
    private final Map<String, TechlogFileDescription> files;
    private final TechlogItemWriter writer;


    public DirectoryDescriptionProcessor(Path path, String serverName, TechlogItemWriter writer) {
        this.path = path;
        this.serverName = serverName;
        this.writer = writer;

        String directoryName = path.getFileName().toString();
        String[] tags = directoryName.split("_");

        this.processType = TechlogProcessType.getByName(tags[0]);
        this.processId = Integer.parseInt(tags[1]);

        files = new HashMap<>();
    }

    public void init() {
        try (Stream<Path> pathStream = Files.find(path, 1, (p, attr) -> p.getFileName().toString().endsWith(".log"))) {
            pathStream.forEach(this::appendLogfile);
        } catch (IOException e) {
            log.debug("Error listing log files directory:", e);
        }

    }

    private TechlogFileDescription appendLogfile(Path filePath) {
        try {
            long fileSize = Files.size(filePath);
            if (fileSize <= 3) { return null; }
        } catch (IOException e) {
            return null;
        }

        TechlogFileDescription fileDescription = new TechlogFileDescription(filePath, processType, processId, serverName, writer);
        files.put(filePath.getFileName().toString(), fileDescription);
        return fileDescription;
    }
}

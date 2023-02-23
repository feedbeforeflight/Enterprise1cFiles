package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class TechlogDescription {

    @Getter
    private final String pathName;
    @Getter
    private final String groupName;
    @Getter
    private final String serverName;
    @Getter
    private final Map<String, TechlogDirectoryDescription> directoryProcessors;
    private final TechlogItemWriter writer;

    public TechlogDescription(String pathName, String groupName, String serverName, TechlogItemWriter writer) {
        this.pathName = pathName;
        this.groupName = groupName;
        this.serverName = serverName;
        this.writer = writer;
        this.directoryProcessors = new HashMap<>();
    }

    public void readFileDescriptions() throws IOException {
        if (pathName.isEmpty()) {
            throw (new IOException("Log path should not be empty"));
        }

        directoryProcessors.values().forEach(techlogDirectoryDescription -> techlogDirectoryDescription.setDirectoryDeleted(true));

        Path path = Paths.get(pathName);
        try (Stream<Path> directoryStream = Files.find(path, 1, (p, attr) -> attr.isDirectory())) {
            directoryStream.skip(1).forEach(this::readDirectory);
        } catch (IOException e) {
            log.debug("Error listing log directory:", e);
        }

        directoryProcessors.entrySet().stream().filter(entry -> entry.getValue().isDirectoryDeleted()).
                forEach(entry -> directoryProcessors.remove(entry.getKey()));
    }

    private void readDirectory(Path directoryPath) {
        String pathString = directoryPath.getFileName().toString();
        TechlogDirectoryDescription directoryProcessor = directoryProcessors.get(pathString);
        if (directoryProcessor == null) {
            directoryProcessor = new TechlogDirectoryDescription(directoryPath, groupName, serverName, writer);
            this.directoryProcessors.put(pathString, directoryProcessor);
        }
        else {
            directoryProcessor.setDirectoryDeleted(false);
        }
        directoryProcessor.readLogfileDescriptions();
    }

}

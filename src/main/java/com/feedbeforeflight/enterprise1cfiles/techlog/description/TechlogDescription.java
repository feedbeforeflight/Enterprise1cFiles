package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final Map<String, TechlogDirectoryDescription> directoryDescriptions;
    private final TechlogItemWriter writer;

    public TechlogDescription(String pathName, String groupName, String serverName, TechlogItemWriter writer) {
        this.pathName = pathName;
        this.groupName = groupName;
        this.serverName = serverName;
        this.writer = writer;
        this.directoryDescriptions = new HashMap<>();
    }

    public void readFileDescriptions() throws IOException {
        if (pathName.isEmpty()) {
            throw (new IOException("Log path should not be empty"));
        }

        directoryDescriptions.values().forEach(techlogDirectoryDescription -> techlogDirectoryDescription.setDirectoryDeleted(true));

        Path path = Paths.get(pathName);
        try (Stream<Path> directoryStream = Files.find(path, 1, (p, attr) -> attr.isDirectory())) {
            directoryStream.skip(1).forEach(this::readDirectory);
        } catch (IOException e) {
            log.debug("Error listing log directory:", e);
        }

        directoryDescriptions.entrySet().stream().filter(entry -> entry.getValue().isDirectoryDeleted()).toList().
                forEach(entry -> directoryDescriptions.remove(entry.getKey()));
        log.debug("Read {} directory descriptions", directoryDescriptions.size());
    }

    private void readDirectory(Path directoryPath) {
        String pathString = directoryPath.getFileName().toString();
        TechlogDirectoryDescription directoryProcessor = directoryDescriptions.get(pathString);
        if (directoryProcessor == null) {
            try {
                directoryProcessor = new TechlogDirectoryDescription(directoryPath, groupName, serverName, writer);
                this.directoryDescriptions.put(pathString, directoryProcessor);
            }
            catch (IllegalArgumentException e) {
                return;
            }
        }
        else {
            directoryProcessor.setDirectoryDeleted(false);
        }
        directoryProcessor.readLogfileDescriptions();
    }

    public int size() {
        return directoryDescriptions.size();
    }

    public TechlogDirectoryDescription get(String name) {
        return directoryDescriptions.get(name);
    }

    public List<TechlogDirectoryDescription> directories() {
        return new ArrayList<>(directoryDescriptions.values());
    }

}

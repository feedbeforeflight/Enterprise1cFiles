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
    private final Map<String, TechlogProcessDirectoryProcessor> directories;
    private final TechlogItemWriter writer;

    public TechlogDescription(String pathName, String groupName, String serverName, TechlogItemWriter writer) {
        this.pathName = pathName;
        this.groupName = groupName;
        this.serverName = serverName;
        this.writer = writer;
        this.directories = new HashMap<>();
    }

    public void refreshFiles() throws IOException {
        if (pathName.isEmpty()) {
            throw (new IOException("Log path should not be empty"));
        }

        directories.clear();

        Path path = Paths.get(pathName);
        try (Stream<Path> directoryStream = Files.find(path, 1, (p, attr) -> attr.isDirectory())) {
            directoryStream.skip(1).forEach(this::appendDirectory);
        } catch (IOException e) {
            log.debug("Error listing log directory:", e);
        }
    }

    private void appendDirectory(Path directoryPath) {
        TechlogProcessDirectoryProcessor directoryProcessor = new TechlogProcessDirectoryProcessor(directoryPath, groupName, serverName, writer);
        directoryProcessor.refreshFiles();
        this.directories.put(directoryPath.getFileName().toString(), directoryProcessor);
    }

}

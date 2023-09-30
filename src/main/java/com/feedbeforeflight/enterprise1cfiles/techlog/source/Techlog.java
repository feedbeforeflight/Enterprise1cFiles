package com.feedbeforeflight.enterprise1cfiles.techlog.source;

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
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class Techlog {

    @Getter
    private final String pathName;
    @Getter
    private final String groupName;
    @Getter
    private final String serverName;
    private final Map<String, TechlogDirectory> directories;
    private final TechlogItemWriter writer;
    Pattern techlogProcessDirectoryFormatPattern = Pattern.compile("^(ragent|rmngr|rphost|ras)\\_\\d*$");

    public Techlog(String pathName, String groupName, String serverName, TechlogItemWriter writer) {
        if (pathName.isEmpty()) { throw (new IllegalArgumentException("Log path should not be empty")); }

        this.pathName = pathName;
        this.groupName = groupName;
        this.serverName = serverName;
        this.writer = writer;
        this.directories = new HashMap<>();
    }

    public void refresh() throws IOException {
        directories.values().forEach(techlogDirectory -> techlogDirectory.setDeleted(true));

        Path path = Paths.get(pathName);
        try (Stream<Path> directoryStream = Files.find(path, 1, (p, attr) -> attr.isDirectory())) {
            directoryStream.skip(1).forEach(this::refreshDirectory);
        } catch (IOException e) {
            log.debug("Error listing log directory:", e);
        }

        directories.entrySet().stream().filter(entry -> entry.getValue().isDeleted()).toList().
                forEach(entry -> directories.remove(entry.getKey()));
        log.debug("Read {} directory descriptions", directories.size());
    }

    private void refreshDirectory(Path directoryPath) {
        String pathString = directoryPath.getFileName().toString();
        if (!directoryNameFormatValid(pathString)) {
            return;
        }
        TechlogDirectory directory = directories.get(pathString);
        if (directory == null) {
            try {
                directory = new TechlogDirectory(directoryPath, groupName, serverName, writer);
                this.directories.put(pathString, directory);
            }
            catch (IllegalArgumentException e) {
                return;
            }
        }
        else {
            directory.setDeleted(false);
        }
        directory.refreshLogFiles();
    }

    public int size() {
        return directories.size();
    }

    public TechlogDirectory get(String name) {
        return directories.get(name);
    }

    public List<TechlogDirectory> directories() {
        return new ArrayList<>(directories.values());
    }

    private boolean directoryNameFormatValid(String directoryName) {
        return techlogProcessDirectoryFormatPattern.matcher(directoryName).matches();
    }
}

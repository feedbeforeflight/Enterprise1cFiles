package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Slf4j
public class TechlogFileDescription implements Comparable<TechlogFileDescription>{

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
    private final String id;
    @Getter
    private final Date timestamp;
    @Getter
    private final String hourString;
//    @Getter @Setter
//    private int linesRead = 0;

    @Getter
    private Date lastRead;

    @Getter @Setter
    private Instant lastLoadedEventTimestamp;

    @Getter @Setter
    private boolean fileDeleted;

    public static String createFileId(Path path, TechlogProcessType processType, int processId) {
        return processType.getName() + "_" + processId + "_" + extractHourString(path);
    }

    private static String extractHourString(Path path) {
        return path.getFileName().toString().substring(0, 8);
    }

    public TechlogFileDescription(Path path, TechlogProcessType processType, int processId, String groupName, String serverName, TechlogItemWriter writer) {
        this.path = path;
        this.processType = processType;
        this.processId = processId;
        this.serverName = serverName;
        this.groupName = groupName;

        hourString = extractHourString(path);
        id = createFileId(path, processType, processId);

//        linesRead = writer.getLinesLoaded(id);

        try {
            this.timestamp = new SimpleDateFormat("yyMMddkk").parse(hourString);
        } catch (ParseException e) {
            log.error("Error parsing log file timestamp from filename: [{}]", hourString);
            throw new IllegalArgumentException("Bad hour string from filename: " + path.getFileName().toString(), e);
        }
    }

    public void updateLastRead() {
        lastRead = new Date();
    }

    @Override
    public int compareTo(TechlogFileDescription o) {
        return timestamp.compareTo(o.getTimestamp());
    }
}

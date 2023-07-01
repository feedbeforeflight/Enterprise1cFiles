package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogFileDescription;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Pattern;

@Slf4j
public class TechlogFileReader implements AutoCloseable {

    private LineNumberReader reader;
    private String recordStartLineBuffer;
    private final Pattern recordStartLinePattern = Pattern.compile("^\\d{2}:\\d{2}.\\d{6}[-]\\d+,[A-Za-z]+,.*");
    private final TechlogFileDescription description;
    private int skippedLines = 0;

    public TechlogFileReader(TechlogFileDescription description) {
        this.description = description;
    }

    public Deque<String> readItemLines() throws IOException {
        LinkedList<String> result = new LinkedList<>();

        String line = recordStartLineBuffer;
        if (line == null || line.isEmpty()) { // empty log file (sometimes only 3 bytes long, when contains only BOM)
            return result;
        }

        while (line != null) {
            result.addLast(line);

            line = reader.readLine();

            if (line == null) {
                recordStartLineBuffer = null;
            }
            if (line != null && line.isEmpty()) {
                recordStartLineBuffer = null;
                line = null;
            };
            if (line != null && recordStartLinePattern.matcher(line).matches()) {
                recordStartLineBuffer = line;
                line = null;
            }
        }

        description.setLinesRead(getLineNumber());
        return result;
    }

    public void openFile() throws IOException {
        reader = new LineNumberReader(new FileReader(description.getPath().toString(), StandardCharsets.UTF_8));

//        if (description.getLinesRead() > 0) {
//            skipToLine(description.getLinesRead());
//        }

        recordStartLineBuffer = reader.readLine();

        if (recordStartLineBuffer != null && recordStartLineBuffer.charAt(0) == '\uFEFF') {
            recordStartLineBuffer = recordStartLineBuffer.substring(1);
        }
    }

    public void closeFile() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    public int getLineNumber() {
        return reader == null ? -1 : reader.getLineNumber();
    }

    public int getSkippedLines() { return skippedLines; }

    public void skipToLine(int linesCount) {
        if (reader != null) {
            try {
                for (int i = reader.getLineNumber(); i < linesCount; i++) {
                    if (reader.readLine() == null) {
                        break;
                    }
                    skippedLines++;
                }
                recordStartLineBuffer = reader.readLine();
            } catch (IOException e) {
                log.error("Exception while skipping lines at file " + description.getId(), e);
            }
        }
    }

    public boolean EOF() {
        return recordStartLineBuffer == null;
    }

    @Override
    public void close() {
        try {
            recordStartLineBuffer = null;
            closeFile();
        }
        catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }
}

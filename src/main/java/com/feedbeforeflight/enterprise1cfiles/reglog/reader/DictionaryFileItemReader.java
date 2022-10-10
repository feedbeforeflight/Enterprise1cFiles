package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class DictionaryFileItemReader extends AbstractLogFileReader {

//    private LineNumberReader reader;
//    private String fileName;

    public DictionaryFileItemReader() {}

    public DictionaryFileItemReader(String fileName) {
        super(fileName);
    }

    @Override
    public void openFile() throws IOException {
        super.openFile();

        // skip 2 non-significant leading lines
        reader.readLine();
        reader.readLine();
    }

    @Override
    protected String readSeparatedLine() throws IOException {
        String line = reader.readLine();
        if (line != null) {
            if (!line.endsWith("},")) {
                String nextLine = reader.readLine();
                if (nextLine != null) {
                    line = line + nextLine;
                }
            }
        }
        return line;
    }

    @Override
    protected List<String> tokenize(String line) {
        int cutFromTheEnd = line.endsWith(",") ? 2 : 1;
        line = line.substring(1, line.length() - cutFromTheEnd);
        return Arrays.asList(line.split(","));
    }

}

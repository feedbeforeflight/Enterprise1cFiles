package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LogFileItemReader extends AbstractLogFileReader {

    private String recordStartLineBuffer;
    private final Pattern recordStartLinePattern = Pattern.compile("^\\{\\d{14},[A-Z],$"); // something like {20220119001056,C,

    public LogFileItemReader() {
    }

    public LogFileItemReader(String fileName) {
        super(fileName);
    }

    public void openFile() throws IOException {
        super.openFile();

        // skip 3 non-significant leading lines
        reader.readLine();
        reader.readLine();
        reader.readLine();
    }

    @Override
    protected String readSeparatedLine() throws IOException {
        StringBuilder result = new StringBuilder();

        String newLine;

        if (recordStartLineBuffer != null) {
            result.append(recordStartLineBuffer);
            recordStartLineBuffer = null;
        } else {
            newLine = reader.readLine();
            if (newLine != null) {
                result.append(newLine);
            }
        }

        do {
            newLine = reader.readLine();

            if (newLine != null) {
                if (recordStartLinePattern.matcher(newLine).matches()) {
                    recordStartLineBuffer = newLine;
                    break;
                }

                result.append(newLine);
            }

        } while (newLine != null);

        return result.length() == 0 ? null : result.toString();
    }

    @Override
    protected List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();

        int start = 1;
        boolean inParenthesis = false;
        int parenthesisCount = 0;
        boolean inQuotes = false;

        for (int i = 1; i < line.length(); i++) {
            char currentChar = line.charAt(i);
            if (currentChar == ',' && !inParenthesis && !inQuotes) {
                tokens.add(line.substring(start, i));
                start = i + 1;
            } else if (currentChar == '{' && !inQuotes) {
                inParenthesis = true;
                parenthesisCount++;
            } else if (currentChar == '}' && !inQuotes) {
                parenthesisCount--;
                inParenthesis = parenthesisCount != 0;
            } else if (currentChar == '"') {
                if (line.charAt(i + 1) == '"') { // double quotes
                    i++;
                    continue;
                } else {
                    inQuotes = !inQuotes;
                }
            }
        }
        tokens.add(line.substring(start, line.length() - (line.charAt(line.length() - 1) == ',' ? 2 : 1)));

        return tokens;
    }

}

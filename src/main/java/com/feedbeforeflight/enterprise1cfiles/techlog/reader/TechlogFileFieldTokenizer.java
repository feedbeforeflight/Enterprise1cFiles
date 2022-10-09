package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import java.util.*;

public class TechlogFileFieldTokenizer {

    public static List<String> readEventTokens(Deque<String> lines) {
        List<String> tokens = new ArrayList<>();

        boolean inApostrophes = false;
        boolean inQuotes = false;

        String line = lines.pollFirst();
        StringBuilder builder = null;

        while (line != null) {
            int start = 0;
            for (int i = 0; i < line.length(); i++) {
                char currentChar = line.charAt(i);
                if (currentChar == ',' && !inApostrophes && !inQuotes) {
                    if (builder == null) {
                        tokens.add(line.substring(start, i));
                    }
                    else {
                        builder.append('\n');
                        builder.append(line.substring(start, i));

                        tokens.add(builder.toString());
                        builder = null;
                    }
                    start = i + 1;
                } else if (currentChar == '\'' && !inQuotes) {
                    inApostrophes = !inApostrophes;
                } else if (currentChar == '\"' && !inApostrophes) {
                    inQuotes = !inQuotes;
                }
            }

            if (!lines.isEmpty()) {
                if (builder == null) {
                    builder = new StringBuilder();
                    builder.append(line.substring(start));
                }
                else {
                    builder.append('\n');
                    builder.append(line.substring(start));
                }
            }
            else {
                if (builder == null) {
                    tokens.add(line.substring(start));
                }
                else {
                    builder.append('\n');
                    builder.append(line.substring(start));

                    tokens.add(builder.toString());
                    builder = null;
                }
            }
            line = lines.pollFirst();
        }

        return tokens;
    }

}

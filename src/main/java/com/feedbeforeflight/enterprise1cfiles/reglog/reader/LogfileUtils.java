package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

public class LogfileUtils {


    public static final long unixTimeSeconds = 62135596800000L;

    public static boolean parenthesisCompleted(String s) {

        int openCount = 0;
        int closeCount = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '{') {
                openCount++;
            }
            else if (s.charAt(i) == '}') {
                closeCount++;
            }
        }

        return openCount == closeCount;
    }

}

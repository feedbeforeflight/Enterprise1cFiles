package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TechlogItemProcessor {

    public static Map<String, String> process(List<String> tokens, TechlogFile techlogFile, int lineNumber) {
        Map<String,String> parameters = new HashMap<>();

        try {
            String token = tokens.get(0);
            parameters.put("_groupname", techlogFile.getGroupName());
            parameters.put("_servername", techlogFile.getServerName());
            parameters.put("_processId", String.valueOf(techlogFile.getProcessId()));
            parameters.put("_fileId", techlogFile.getId());

            parameters.put("_linenumber", String.valueOf(lineNumber));
            parameters.put("_timestamp", techlogFile.getHourString() + ":" + token.substring(0, 5));
            parameters.put("_timepart", token.substring(6, 12));
            parameters.put("_duration", token.substring(13));

            parameters.put("_type", tokens.get(1));
            parameters.put("_level", tokens.get(2));

            tokens.stream().skip(3).forEach(s -> {
                int pos = s.indexOf('=');
                parameters.put(s.substring(0, pos), s.substring(pos + 1));
            });
        }
        catch (Exception e) {
            log.error("Processing error. Tokens: " + tokens.toString(), e);
        }
        return parameters;
    }
}

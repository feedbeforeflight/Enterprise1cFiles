package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Slf4j
public abstract class AbstractTechlogEventFieldMapper {

//    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddkk:mm:ss");

    protected abstract void mapSpecificFields(AbstractTechlogEvent event, Map<String,String> parameters, AbstractTechlogEvent prevEvent);

//    public AbstractTechlogEvent mapFromTokens(AbstractTechlogEvent event, List<String> tokens, TechlogFileDescription description, int lineNumber, AbstractTechlogEvent prevEvent) {
//
//        event.setLineNumber(lineNumber);
//
//        String token = tokens.get(0);
//        try {
//            event.setTimestamp(simpleDateFormat.parse(description.getHourString() + ":" + token.substring(0, 5)));
//        } catch (ParseException e) {
//            log.error("Error parsing event timestamp: [{}]", token.substring(0, 5));
//            return null;
//        }
//        event.setTimepart(Integer.parseInt(token.substring(6, 12)));
//        event.setDuration(Long.parseLong(token.substring(13)));
//
//        event.setType(TechlogEventType.getByName(tokens.get(1)));
//        event.setLevel(Integer.parseInt(tokens.get(2)));
//
//        Map<String,String> parameters = new HashMap<>();
//        tokens.stream().skip(3).forEach(s -> {
//            int pos = s.indexOf('=');
//            parameters.put(s.substring(0, pos), s.substring(pos + 1));
//        });
//
//        parameters.forEach((key, value) -> {
//            switch (key) {
//                case "process":
//                    event.setProcessType(TechlogProcessType.getByName(value));
//                    break;
//                case "p:processName":
//                    event.setIbName(value);
//                    break;
//                case "t:clientID":
//                    event.setConnectionID(Integer.parseInt(value));
//                    break;
//                case "sessionID":
//                    event.setSessionID(Integer.parseInt(value));
//                    break;
//                case "Usr":
//                    event.setUserName(value);
//                    break;
//                case "t:connectID":
//                    event.setDatabaseSessionID(value);
//                    break;
//                case "t:applicationName":
//                    event.setApplication(value);
//                    break;
//                case "t:computerName":
//                    event.setComputer(value);
//                    break;
//                case "Context":
//                    event.setContext(removeQuotes(value));
//                    break;
//            }
//        });
//
////        if (event.getType() == TechlogEventType.CONTEXT || event.getType() == TechlogEventType.EXCPCNTX) {
////            event.setParentEvent(prevEvent);
////        }
//
//        mapSpecificFields(event, parameters, prevEvent);
//
//        return event;
//    }

    public AbstractTechlogEvent map(AbstractTechlogEvent event, Map<String, String> parameters, AbstractTechlogEvent prevEvent) {
                event.setLineNumber(Integer.parseInt(parameters.get("_linenumber")));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddkk:mm:ss");

        try {
            long timepartNanos = Integer.parseInt(parameters.get("_timepart")) * 1000L;
            event.setTimestamp(simpleDateFormat.parse(parameters.get("_timestamp")).toInstant().plusNanos(timepartNanos));
        } catch (ParseException e) {
            log.error("Error parsing event timestamp: [{}]", parameters.get("_timestamp"));
            return null;
        }
        event.setGroup(parameters.get("_groupname"));
        event.setServerName(parameters.get("_servername"));
        event.setProcessId(parameters.get("_processId"));
        event.setFileId(parameters.get("_fileId"));

        //event.setTimepart(Integer.parseInt(parameters.get("_timepart")));
        event.setDuration(Long.parseLong(parameters.get("_duration")));

        //event.setType(TechlogEventType.getByName(parameters.get("_type")));
        event.setLevel(Integer.parseInt(parameters.get("_level")));

        parameters.forEach((key, value) -> {
            switch (key) {
                case "process":
                    event.setProcessType(TechlogProcessType.getByName(value));
                    break;
                case "p:processName":
                    event.setIbName(value.toLowerCase());
                    break;
                case "t:connectID":
                    event.setConnectionID(Integer.parseInt(value));
                    break;
                case "sessionID":
                    event.setSessionID(Integer.parseInt(value));
                    break;
                case "Usr":
                    event.setUserName(value);
                    break;
                case "t:clientID":
                    event.setDatabaseSessionID(value);
                    break;
                case "t:applicationName":
                    event.setApplication(value);
                    break;
                case "t:computerName":
                    event.setComputer(value);
                    break;
                case "Context":
                    try {
                        event.setContext(trimLeadingNewline(removeQuotes(value)));
                    }
                    catch (StringIndexOutOfBoundsException e) {
                        log.error("Error setting context: [{}] at line [{}]", value, event.getLineNumber());
                    }
                    break;
            }
        });

        mapSpecificFields(event, parameters, prevEvent);

        return event;
    }

    public static String removeQuotes(String string) {
        if (string.isEmpty() || string.length() == 1) {
            return string;
        }

        if (string.equals("\"\"")) { return ""; }
        else if (string.equals("''")) { return ""; }
        else {
            if ((string.charAt(0) == '\'' && string.charAt(string.length() - 1) == '\'') ||
                    (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"')){
                return string.substring(1, string.length() - 1);
            }
            else {
                return string;
            }
        }
    }

    public static String trimLeadingNewline(String string) {
        return string.replaceFirst("^\\n", "");
    }

}

package com.feedbeforeflight.enterprise1cfiles.techlog.data;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TechlogEventType {
    DBMSSQL,
    EXCP,
    EXCPCNTX,
    CONTEXT,
    TLOCK,
    TTIMEOUT,
    TDEADLOCK,

    // rarely used in real - for test to correct handling of unimplemented events
    // kind of temporary, only while there is events in enum, not implemented in reader chain
    // events not in this enum will be ignored by design
    CONFLOADFROMFILES;

    private static final Map<String, TechlogEventType> lookup =
            Arrays.stream(values()).collect(Collectors.toMap(Enum::name, Function.identity()));

    public static TechlogEventType getByName(String name) {
        return lookup.get(name.toUpperCase());
    }
}

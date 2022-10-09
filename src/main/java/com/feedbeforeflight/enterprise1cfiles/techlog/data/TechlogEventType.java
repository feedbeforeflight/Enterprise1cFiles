package com.feedbeforeflight.enterprise1cfiles.techlog.data;

public enum TechlogEventType {
    DBMSSQL,
    EXCP,
    EXCPCNTX,
    CONTEXT,
    TLOCK,
    TTIMEOUT,
    TDEADLOCK;


    public static TechlogEventType getByName(String name) {
        return valueOf(name.toUpperCase());
    }
}

package com.feedbeforeflight.enterprise1cfiles.techlog.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public enum TechlogProcessType {

    RAGENT("ragent"),
    RMNGR("rmngr"),
    RPHOST("rphost"),
    RAS("ras");

    @Getter
    private final String name;

    public static TechlogProcessType getByName(String name) {
        try {
            return valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            log.error("Unknown techlog process type name: " + name, e);
            throw new IllegalArgumentException("Unknown techlog process type name: " + name);
        }
    }

}

package com.feedbeforeflight.enterprise1cfiles.techlog.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TechlogProcessType {

    RAGENT("ragent"),
    RMNGR("rmngr"),
    RPHOST("rphost"),
    RAS("ras");

    @Getter
    private final String name;

    public static TechlogProcessType getByName(String name) {
        return valueOf(name.toUpperCase());
    }

}

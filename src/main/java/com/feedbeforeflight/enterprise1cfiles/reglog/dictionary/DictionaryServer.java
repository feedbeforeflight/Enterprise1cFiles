package com.feedbeforeflight.enterprise1cfiles.reglog.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DictionaryServer {

    @Getter
    private final int id;
    @Getter
    private final String name;

    public String Presentation() {
        return name;
    }
}

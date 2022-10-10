package com.feedbeforeflight.enterprise1cfiles.reglog.dictionary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DictionaryUser {

    @Getter
    private final int id;
    @Getter
    private final String guid;
    @Getter
    private final String name;

    public String Presentation() {
        return name;
    }
}

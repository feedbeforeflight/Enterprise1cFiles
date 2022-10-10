package com.feedbeforeflight.enterprise1cfiles.reglog.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogfileEventImportance {
    INFORMATION("Информация"),
    ERROR("Ошибка"),
    WARNING("Предупреждение"),
    NOTE("Примечание");

    @Getter
    private final String name;

    public static LogfileEventImportance getByLogID(String logID) {
        switch (logID) {
            case ("I"): return INFORMATION;
            case ("E"): return ERROR;
            case ("W"): return WARNING;
            case ("N"): return NOTE;
            default: return null;
        }
    }
}

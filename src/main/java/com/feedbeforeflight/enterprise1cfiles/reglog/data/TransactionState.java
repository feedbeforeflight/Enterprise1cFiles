package com.feedbeforeflight.enterprise1cfiles.reglog.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TransactionState {
    NONE("Отсутствует"),
    UPDATED("Зафиксирована"),
    RUNNING("Не завершена"),
    CANCELLED("Отменена");

    @Getter
    private final String name;

    public static TransactionState getByLogID(String logID) {
        switch(logID) {
            case ("N"): return NONE;
            case ("U"): return UPDATED;
            case ("R"): return RUNNING;
            case ("C"): return CANCELLED;
            default: return null;
        }
    }
}

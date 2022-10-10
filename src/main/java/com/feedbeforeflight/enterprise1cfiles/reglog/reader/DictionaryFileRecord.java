package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class DictionaryFileRecord {

    @Getter @Setter
    private int typeId;
    @Getter @Setter
    private String[] fields;

    public DictionaryFileRecord(int typeId) {
        this.typeId = typeId;
    }

    public DictionaryFileRecord(int typeId, int fieldCount) {
        this.typeId = typeId;
        this.fields = new String[fieldCount];
    }

    @Override
    public String toString() {
        return "DictionaryRecord{" +
                "typeId=" + typeId +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }
}
//1 – пользователи;
//2 – компьютеры;
//3 – приложения;
//4 – события;
//5 – метаданные;
//6 – серверы;
//7 – основные порты;
//8 – вспомогательные порты.
package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import java.util.List;

public class DictionaryFileFieldsMapper {

    public static DictionaryFileRecord mapFields(List<String> fields) {
        if (fields == null) {return null;}

        //Assert.notEmpty(fields, "Field array should not be empty.");

        DictionaryFileRecord dictionaryRecord = new DictionaryFileRecord(Integer.parseInt(fields.get(0)), fields.size() - 1);
        dictionaryRecord.setFields(fields.stream().skip(1).toArray(String[]::new));

        return dictionaryRecord;
    }
}

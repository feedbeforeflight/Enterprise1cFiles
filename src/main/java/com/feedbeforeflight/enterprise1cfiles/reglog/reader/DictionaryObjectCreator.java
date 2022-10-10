package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import com.feedbeforeflight.enterprise1cfiles.reglog.dictionary.*;

public class DictionaryObjectCreator {

    private final Dictionary dictionary;

    public DictionaryObjectCreator(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    public void addItem(DictionaryFileRecord dictionaryFileRecord) {
        switch (dictionaryFileRecord.getTypeId()) {
            case 1:
                addUser(dictionaryFileRecord.getFields());
                break;
            case 2:
                addComputer(dictionaryFileRecord.getFields());
                break;
            case 3:
                addApplication(dictionaryFileRecord.getFields());
                break;
            case 4:
                addEvent(dictionaryFileRecord.getFields());
                break;
            case 5:
                addMetadata(dictionaryFileRecord.getFields());
                break;
            case 6:
                addServer(dictionaryFileRecord.getFields());
                break;
            case 7:
                addMainPort(dictionaryFileRecord.getFields());
                break;
            case 8:
                addAuxiliaryPort(dictionaryFileRecord.getFields());
                break;
        }
    }

    private void addUser(String[] fields) {
        DictionaryUser user = new DictionaryUser(Integer.parseInt(fields[2]), fields[0], fields[1]);
        dictionary.addUser(user);
    }

    private void addComputer(String[] fields) {
        DictionaryComputer computer = new DictionaryComputer(Integer.parseInt(fields[1]), fields[0]);
        dictionary.addComputer(computer);
    }

    private void addApplication(String[] fields) {
        DictionaryApplication application = new DictionaryApplication(Integer.parseInt(fields[1]), fields[0]);
        dictionary.addApplication(application);
    }

    private void addEvent(String[] fields) {
        DictionaryEvent event = new DictionaryEvent(Integer.parseInt(fields[1]), fields[0]);
        dictionary.addEvent(event);
    }

    private void addMetadata(String[] fields) {
        DictionaryMetadata metadata = new DictionaryMetadata(Integer.parseInt(fields[2]), fields[0], fields[1]);
        dictionary.addMetadata(metadata);
    }

    private void addServer(String[] fields) {
        DictionaryServer server = new DictionaryServer(Integer.parseInt(fields[1]), fields[0]);
        dictionary.addServer(server);
    }

    private void addMainPort(String[] fields) {
        DictionaryMainPort port = new DictionaryMainPort(Integer.parseInt(fields[1]), Integer.parseInt(fields[0]));
        dictionary.addMainPort(port);
    }

    private void addAuxiliaryPort(String[] fields) {
        DictionaryAuxiliaryPort port = new DictionaryAuxiliaryPort(Integer.parseInt(fields[1]), Integer.parseInt(fields[0]));
        dictionary.addAuxiliaryPort(port);
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

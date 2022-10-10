package com.feedbeforeflight.enterprise1cfiles.reglog.dictionary;

import java.util.HashMap;


public class Dictionary {

    private final HashMap<Integer, DictionaryUser> userMap;
    private final HashMap<Integer, DictionaryComputer> computerMap;
    private final HashMap<Integer, DictionaryApplication> applicationMap;
    private final HashMap<Integer, DictionaryEvent> eventMap;
    private final HashMap<Integer, DictionaryMetadata> metadataMap;
    private final HashMap<Integer, DictionaryServer> serverMap;
    private final HashMap<Integer, DictionaryMainPort> mainPortMap;
    private final HashMap<Integer, DictionaryAuxiliaryPort> auxiliaryPortMap;

    public Dictionary() {
        userMap = new HashMap<>();
        computerMap = new HashMap<>();
        applicationMap = new HashMap<>();
        eventMap = new HashMap<>();
        metadataMap = new HashMap<>();
        serverMap = new HashMap<>();
        mainPortMap = new HashMap<>();
        auxiliaryPortMap = new HashMap<>();
    }

    public String summary() {
        return "Users: " + userMap.size() + System.lineSeparator() +
                "Computers: " + computerMap.size() + System.lineSeparator() +
                "Applications: " + applicationMap.size() + System.lineSeparator() +
                "Events: " + eventMap.size() + System.lineSeparator() +
                "Metadata items: " + metadataMap.size() + System.lineSeparator() +
                "Servers: " + serverMap.size() + System.lineSeparator() +
                "Main ports: " + mainPortMap.size() + System.lineSeparator() +
                "Auxiliary ports: " + auxiliaryPortMap.size() + System.lineSeparator();
    }

    public void addUser(DictionaryUser user) {
        userMap.put(user.getId(), user);
    }

    public DictionaryUser getUser(int id) {
        return userMap.get(id);
    }

    public void addComputer(DictionaryComputer computer) {
        computerMap.put(computer.getId(), computer);
    }

    public DictionaryComputer getComputer(int id) {
        return computerMap.get(id);
    }

    public void addApplication(DictionaryApplication application) {
        applicationMap.put(application.getId(), application);
    }

    public DictionaryApplication getApplication(int id) {
        return applicationMap.get(id);
    }

    public void addEvent(DictionaryEvent event) {
        eventMap.put(event.getId(), event);
    }

    public DictionaryEvent getEvent(int id) {
        return eventMap.get(id);
    }

    public void addMetadata(DictionaryMetadata metadata) {
        metadataMap.put(metadata.getId(), metadata);
    }

    public DictionaryMetadata getMetadata(int id) {
        return metadataMap.get(id);
    }

    public void addServer(DictionaryServer server) {
        serverMap.put(server.getId(), server);
    }

    public DictionaryServer getServer(int id) {
        return serverMap.get(id);
    }

    public void addMainPort(DictionaryMainPort port) {
        mainPortMap.put(port.getId(), port);
    }

    public DictionaryMainPort getMainPort(int id) {
        return mainPortMap.get(id);
    }

    public void addAuxiliaryPort(DictionaryAuxiliaryPort port) {
        auxiliaryPortMap.put(port.getId(), port);
    }

    public DictionaryAuxiliaryPort getAuxiliaryPort(int id) {
        return auxiliaryPortMap.get(id);
    }
}

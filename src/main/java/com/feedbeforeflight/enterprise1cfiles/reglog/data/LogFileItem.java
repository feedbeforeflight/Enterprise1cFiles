package com.feedbeforeflight.enterprise1cfiles.reglog.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class LogFileItem {

    @Getter
    @Setter
    protected String fileName;
    @Getter
    @Setter
    protected int rowNumber;
    @Getter
    @Setter
    protected Date timestamp;
    @Getter
    @Setter
    protected TransactionState transactionState;
    @Getter
    @Setter
    protected Date transactionDate;
    @Getter
    @Setter
    protected int transactionNumber = 0;
    @Getter
    @Setter
    protected String username;
    @Getter
    @Setter
    protected String computer;
    @Getter
    @Setter
    protected String application;
    @Getter
    @Setter
    protected int connection;
    @Getter
    @Setter
    protected String event;
    @Getter
    @Setter
    protected LogfileEventImportance eventImportance;
    @Getter
    @Setter
    protected String comment;
    @Getter
    @Setter
    protected String metadata;
    @Getter
    @Setter
    protected String data;
    @Getter
    @Setter
    protected String dataRepresentation;
    @Getter
    @Setter
    protected String server;
    @Getter
    @Setter
    protected int mainPort;
    @Getter
    @Setter
    protected int auxiliaryPort;
    @Getter
    @Setter
    protected int session;
    @Getter
    @Setter
    private String databaseName;
    @Getter
    @Setter
    private String userGUID;
    @Getter
    @Setter
    private String metadataGUID;


}

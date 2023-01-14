package com.feedbeforeflight.enterprise1cfiles.techlog.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public abstract class AbstractTechlogEvent {

    protected String group;
    protected Instant recordTime;
    protected String serverName;
    protected TechlogProcessType processType; // process
    protected String processId;
    protected String fileId;

    protected int lineNumber;
    protected Instant timestamp;
    protected long duration;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static TechlogEventType type; // from name
    protected int level;

    protected String ibName; // p:processName
    protected int sessionID; // sessionID
    protected int connectionID; // t:clientID
    protected String computer; // t:computerName
    protected String application; // t:applicationName, AppID
    protected String userName; // Usr
    protected String databaseSessionID; // t:connectID

    protected String context; // Context

    protected UUID groupId;

    public abstract TechlogEventType getType();

    public String toString() {
        return serverName + "-" + processType + "-" + processId + "-" + lineNumber + " " +
                timestamp +
                " duration:" + duration +
                " " + getType() +
                " L" + level;
    }
}

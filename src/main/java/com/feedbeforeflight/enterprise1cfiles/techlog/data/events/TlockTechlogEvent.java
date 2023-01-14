package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TlockTechlogEvent extends AbstractTechlogEvent {

    protected String regions;
    protected String locks;
    protected String waitConnections;

    public static TechlogEventType classType() {
        return TechlogEventType.TLOCK;
    }

    @Override
    public TechlogEventType getType() {
        return classType();
    }
}

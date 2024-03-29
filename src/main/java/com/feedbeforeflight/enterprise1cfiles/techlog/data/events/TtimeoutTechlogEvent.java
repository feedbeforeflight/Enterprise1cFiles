package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TtimeoutTechlogEvent extends AbstractTechlogEvent {

    protected String waitConnections;

    public static TechlogEventType classType() {
        return TechlogEventType.TTIMEOUT;
    }

    @Override
    public TechlogEventType getType() {
        return classType();
    }
}

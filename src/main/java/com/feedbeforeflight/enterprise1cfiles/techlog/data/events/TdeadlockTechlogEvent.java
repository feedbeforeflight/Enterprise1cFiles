package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TdeadlockTechlogEvent extends AbstractTechlogEvent {

    protected String deadlockConnectionIntersections;

    public static TechlogEventType classType() {
        return TechlogEventType.TDEADLOCK;
    }

    @Override
    public TechlogEventType getType() {
        return classType();
    }
}

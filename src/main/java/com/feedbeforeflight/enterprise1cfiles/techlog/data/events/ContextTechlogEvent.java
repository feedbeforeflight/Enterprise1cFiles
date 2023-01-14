package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;

public class ContextTechlogEvent extends AbstractTechlogEvent {

    public static TechlogEventType classType() {
        return TechlogEventType.CONTEXT;
    }

    @Override
    public TechlogEventType getType() {
        return classType();
    }
}

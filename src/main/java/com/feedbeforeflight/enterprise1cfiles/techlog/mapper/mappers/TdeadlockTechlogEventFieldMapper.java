package com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TdeadlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;

import java.util.Map;

public class TdeadlockTechlogEventFieldMapper extends AbstractTechlogEventFieldMapper {
    @Override
    protected void mapSpecificFields(AbstractTechlogEvent event, Map<String, String> parameters, AbstractTechlogEvent prevEvent) {
        TdeadlockTechlogEvent specificEvent = (TdeadlockTechlogEvent) event;

        parameters.forEach((key, value) -> {
            if ("DeadlockConnectionIntersections".equals(key)) {
                specificEvent.setDeadlockConnectionIntersections(removeQuotes(value));
            }
        });
    }
}

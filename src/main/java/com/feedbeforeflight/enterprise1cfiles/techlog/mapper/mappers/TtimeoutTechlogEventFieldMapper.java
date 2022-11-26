package com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TtimeoutTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;

import java.util.Map;

public class TtimeoutTechlogEventFieldMapper extends AbstractTechlogEventFieldMapper {
    @Override
    protected void mapSpecificFields(AbstractTechlogEvent event, Map<String, String> parameters, AbstractTechlogEvent prevEvent) {
        TtimeoutTechlogEvent specificEvent = (TtimeoutTechlogEvent) event;

        parameters.forEach((key, value) -> {
            if ("WaitConnections".equals(key)) {
                specificEvent.setWaitConnections(removeQuotes(value));
            }
        });

    }
}

package com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;

import java.util.Map;

public class TlockTechlogEventFieldMapper extends AbstractTechlogEventFieldMapper {

    @Override
    protected void mapSpecificFields(AbstractTechlogEvent event, Map<String, String> parameters, AbstractTechlogEvent prevEvent) {
        TlockTechlogEvent tlockEvent = (TlockTechlogEvent) event;

        parameters.forEach((key, value) -> {
              switch (key) {
                case "process":
                    tlockEvent.setRegions(value);
                    break;
                case "p:processName":
                    tlockEvent.setLocks(value);
                    break;
                case "WaitConnections":
                    tlockEvent.setWaitConnections(value);
                    break;
            }
        });
    }

}

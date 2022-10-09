package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.ContextTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TdeadlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TtimeoutTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.TechlogEventFieldMapperFactory;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public class TechlogEventFactory {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddkk:mm:ss");
    private final String server; // TODO delete
    private final String cluster; // TODO delete
    private final EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mapperMap;

    public TechlogEventFactory(String cluster, String server) {
        this.server = server;
        this.cluster = cluster;

        mapperMap = TechlogEventFieldMapperFactory.getAllMappers();
    }

    private AbstractTechlogEvent getEventByType(TechlogEventType type) {
        // add new events only here
        AbstractTechlogEvent event = switch (type) {
            case TLOCK -> new TlockTechlogEvent();
            case TTIMEOUT -> new TtimeoutTechlogEvent();
            case TDEADLOCK -> new TdeadlockTechlogEvent();
            case CONTEXT -> new ContextTechlogEvent();
            default -> null;
        };
        
        return event;
    }

    public AbstractTechlogEvent createEvent(Map<String, String> parameters, AbstractTechlogEvent prevEvent) {

        TechlogEventType eventType = TechlogEventType.getByName(parameters.get("_type"));

        AbstractTechlogEvent event = getEventByType(eventType);
        if (event == null) {
            return null;
        }

        AbstractTechlogEventFieldMapper mapper = mapperMap.get(event.getType());
        if (mapper == null) {
            return null;
        }

        mapper.map(event, parameters, prevEvent);

        return event;
    }
}

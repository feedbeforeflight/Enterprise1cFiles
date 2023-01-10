package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.ContextTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TdeadlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TtimeoutTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.TechlogEventFieldMapperFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TechlogEventFactory {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddkk:mm:ss");
    private final String server; // TODO delete
    private final String cluster; // TODO delete
    @Getter
    private final EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mapperMap;
    @Getter
    private final EnumMap<TechlogEventType, Class<? extends AbstractTechlogEvent>> classesMap;

    public TechlogEventFactory(String cluster, String server) {
        this.server = server;
        this.cluster = cluster;

        mapperMap = TechlogEventFieldMapperFactory.getAllMappers();
        classesMap = createClassesMap();
    }

    private static EnumMap<TechlogEventType, Class<? extends AbstractTechlogEvent>> createClassesMap() {
        EnumMap<TechlogEventType, Class<? extends AbstractTechlogEvent>> result = new EnumMap<>(TechlogEventType.class);

        Class<AbstractTechlogEvent> parentEventClass = AbstractTechlogEvent.class;
        String concreteEventsPackageName = parentEventClass.getPackageName() + ".events";

        Reflections reflections = new Reflections(concreteEventsPackageName, Scanners.SubTypes);
        Set<Class<? extends AbstractTechlogEvent>> eventClasses = reflections.getSubTypesOf(parentEventClass);

        for (Class<? extends AbstractTechlogEvent> eventClass: eventClasses) {
            try {
                Method methodClassType = eventClass.getMethod("classType");
                TechlogEventType classType = (TechlogEventType) methodClassType.invoke(null);
                result.put(classType, eventClass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to get method classType from AbstractTechlogEvent descendant " + eventClass.getName(), e);
            }
        }

        return result;
    }

    private AbstractTechlogEvent getEventByType(TechlogEventType type) {
        // add new events only here

//        return switch (type) {
//            case TLOCK -> new TlockTechlogEvent();
//            case TTIMEOUT -> new TtimeoutTechlogEvent();
//            case TDEADLOCK -> new TdeadlockTechlogEvent();
//            case CONTEXT -> new ContextTechlogEvent();
//            default -> null;
//        };
        Class<? extends AbstractTechlogEvent> eventClass = classesMap.get(type);
        try {
            return (AbstractTechlogEvent) eventClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create new instance of class " + eventClass.getName(), e);
        }
        return null;
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

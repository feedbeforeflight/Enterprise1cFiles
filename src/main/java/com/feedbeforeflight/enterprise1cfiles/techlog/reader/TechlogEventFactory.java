package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.TechlogEventFieldMapperFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class TechlogEventFactory {

    static{
        mappers = TechlogEventFieldMapperFactory.getAllMappers();
        eventClasses = createClassesMap();
    }

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddkk:mm:ss");
    private final String server; // TODO delete
    private final String cluster; // TODO delete
    @Getter
    private static final EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mappers;
    @Getter
    private static final EnumMap<TechlogEventType, Class<? extends AbstractTechlogEvent>> eventClasses;

    public TechlogEventFactory(String cluster, String server) {
        this.server = server;
        this.cluster = cluster;
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
        Class<? extends AbstractTechlogEvent> eventClass = eventClasses.get(type);
        if (eventClass == null) {
            return null;
        }
        try {
            return eventClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create new instance of class " + eventClass.getName(), e);
        }
        return null;
    }

    public AbstractTechlogEvent createEvent(Map<String, String> parameters, AbstractTechlogEvent prevEvent) {

        TechlogEventType eventType = TechlogEventType.getByName(parameters.get("_type"));
        if (eventType == null) {
            return null;
        }

        AbstractTechlogEvent event = getEventByType(eventType);
        if (event == null) {
            return null;
        }

        AbstractTechlogEventFieldMapper mapper = mappers.get(event.getType());
        if (mapper == null) {
            return null;
        }

        mapper.map(event, parameters, prevEvent);

        return event;
    }
}

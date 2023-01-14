package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TlockTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.ContextTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TdeadlockTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TtimeoutTechlogEventFieldMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class TechlogEventFieldMapperFactory {

    static {
        availableMapperClasses = createClassesMap();
    }

    @Getter
    private static final EnumMap<TechlogEventType, Class<? extends AbstractTechlogEventFieldMapper>> availableMapperClasses;

    private final EnumSet<TechlogEventType> selectedTypes;

    private static Optional<AbstractTechlogEventFieldMapper> mapperOfType(TechlogEventType type) {
        // add new event mappers only here
//        AbstractTechlogEventFieldMapper mapper = switch(type) {
//            case TLOCK -> new TlockTechlogEventFieldMapper();
//            case TTIMEOUT -> new TtimeoutTechlogEventFieldMapper();
//            case TDEADLOCK -> new TdeadlockTechlogEventFieldMapper();
//            case CONTEXT -> new ContextTechlogEventFieldMapper();
//            default -> null;
//        };
//        return Optional.ofNullable(mapper);
        Class<? extends AbstractTechlogEventFieldMapper> mapperClass = availableMapperClasses.get(type);
        try {
            return Optional.of(mapperClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create new instance of class " + mapperClass.getName(), e);
        }
        return Optional.empty();
    }

    private static EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> getMappers(EnumSet<TechlogEventType> typeSet) {
        EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mapperMap = new EnumMap<>(TechlogEventType.class);

        for (TechlogEventType type : typeSet) {
            mapperOfType(type).ifPresent(mapper -> mapperMap.put(type, mapper));
        }

        return mapperMap;
    }

    private static EnumMap<TechlogEventType, Class<? extends AbstractTechlogEventFieldMapper>> createClassesMap() {
        EnumMap<TechlogEventType, Class<? extends AbstractTechlogEventFieldMapper>> result = new EnumMap<>(TechlogEventType.class);

        Class<AbstractTechlogEventFieldMapper> parentMapperClass = AbstractTechlogEventFieldMapper.class;
        String concreteMappersPackageName = parentMapperClass.getPackageName() + ".mappers";

        Reflections reflections = new Reflections(concreteMappersPackageName, Scanners.SubTypes);
        Set<Class<? extends AbstractTechlogEventFieldMapper>> mapperClasses = reflections.getSubTypesOf(parentMapperClass);

        for (Class<? extends AbstractTechlogEventFieldMapper> mapperClass: mapperClasses) {
            try {
                Method methodClassType = mapperClass.getMethod("classType");
                TechlogEventType classType = (TechlogEventType) methodClassType.invoke(null);
                result.put(classType, mapperClass);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to get method classType from AbstractTechlogEvent descendant " + mapperClass.getName(), e);
            }
        }

        return result;
    }

    public static EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> getAllMappers() {
        return getMappers(EnumSet.allOf(TechlogEventType.class));
    }


    // дичь. убрать/переделать.

    public static TechlogEventFieldMapperFactory of() {
        return new TechlogEventFieldMapperFactory();
    }

    private TechlogEventFieldMapperFactory() {
        this.selectedTypes = EnumSet.noneOf(TechlogEventType.class);
    }

    public TechlogEventFieldMapperFactory type(TechlogEventType type) {
        this.selectedTypes.add(type);

        return this;
    }

    public EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> build() {
        return getMappers(selectedTypes);
    }

}

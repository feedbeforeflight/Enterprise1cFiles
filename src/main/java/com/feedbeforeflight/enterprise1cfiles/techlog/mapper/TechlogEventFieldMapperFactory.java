package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.*;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Optional;

public class TechlogEventFieldMapperFactory {

    private final EnumSet<TechlogEventType> selectedTypes;

    private static Optional<AbstractTechlogEventFieldMapper> mapperOfType(TechlogEventType type) {
        // add new event mappers only here
        AbstractTechlogEventFieldMapper mapper = switch(type) {
            case TLOCK -> new TlockTechlogEventFieldMapper();
            case TTIMEOUT -> new TtimeoutTechlogEventFieldMapper();
            case TDEADLOCK -> new TdeadlockTechlogEventFieldMapper();
            case CONTEXT -> new ContextTechlogEventFieldMapper();
            case DBMSSQL -> new DbmssqlTechlogEventFieldMapper();
            default -> null;
        };
        return Optional.ofNullable(mapper);
    }

    private static EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> getMappers(EnumSet<TechlogEventType> typeSet) {
        EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mapperMap = new EnumMap<>(TechlogEventType.class);

        for (TechlogEventType type : typeSet) {
            mapperOfType(type).ifPresent(mapper -> mapperMap.put(type, mapper));
        }

        return mapperMap;
    }

    public static EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> getAllMappers() {
        return getMappers(EnumSet.allOf(TechlogEventType.class));
    }

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

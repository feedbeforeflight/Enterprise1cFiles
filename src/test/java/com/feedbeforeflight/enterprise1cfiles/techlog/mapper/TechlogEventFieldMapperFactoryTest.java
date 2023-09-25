package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.*;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogEventFieldMapperFactoryTest {


    @Test
    void availableMapperClasses_ShouldCollectAllConcreteMapperClasses() {
        EnumMap<TechlogEventType, Class<? extends AbstractTechlogEventFieldMapper>> mappers = TechlogEventFieldMapperFactory.getAvailableMapperClasses();

        assertThat(mappers.size(), equalTo(5));
        assertThat(mappers.get(TechlogEventType.CONTEXT), is(ContextTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TDEADLOCK), is(TdeadlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TLOCK), is(TlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TTIMEOUT), is(TtimeoutTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.DBMSSQL), is(DbmssqlTechlogEventFieldMapper.class));
    }

    @Test
    void getAllMappers_ShouldSucceed() {
        EnumMap<TechlogEventType, AbstractTechlogEventFieldMapper> mappers = TechlogEventFieldMapperFactory.getAllMappers();
        assertThat(mappers.size(), equalTo(5));
        assertThat(mappers.get(TechlogEventType.CONTEXT), instanceOf(ContextTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TDEADLOCK), instanceOf(TdeadlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TLOCK), instanceOf(TlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TTIMEOUT), instanceOf(TtimeoutTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.DBMSSQL), instanceOf(DbmssqlTechlogEventFieldMapper.class));
    }

}
package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.ContextTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TdeadlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TtimeoutTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.ContextTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TdeadlockTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TlockTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers.TtimeoutTechlogEventFieldMapper;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogEventFactory;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class TechlogEventFieldMapperFactoryTest {


    @Test
    void availableMapperClasses_ShouldCollectAllConcreteMapperClasses() {
        EnumMap<TechlogEventType, Class<? extends AbstractTechlogEventFieldMapper>> mappers = TechlogEventFieldMapperFactory.getAvailableMapperClasses();

        assertThat(mappers.size(), equalTo(4));
        assertThat(mappers.get(TechlogEventType.CONTEXT), is(ContextTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TDEADLOCK), is(TdeadlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TLOCK), is(TlockTechlogEventFieldMapper.class));
        assertThat(mappers.get(TechlogEventType.TTIMEOUT), is(TtimeoutTechlogEventFieldMapper.class));
    }

}
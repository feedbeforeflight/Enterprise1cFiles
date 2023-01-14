package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.ContextTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TdeadlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TlockTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.TtimeoutTechlogEvent;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogEventFactoryTest {

    @Test
    void createObject_ShouldCollectAllConcreteEventClasses() {
        TechlogEventFactory factory = new TechlogEventFactory("cluster", "server");

        assertThat(factory.getEventClasses().size(), equalTo(4));
        assertThat(factory.getEventClasses().get(TechlogEventType.CONTEXT), is(ContextTechlogEvent.class));
        assertThat(factory.getEventClasses().get(TechlogEventType.TDEADLOCK), is(TdeadlockTechlogEvent.class));
        assertThat(factory.getEventClasses().get(TechlogEventType.TLOCK), is(TlockTechlogEvent.class));
        assertThat(factory.getEventClasses().get(TechlogEventType.TTIMEOUT), is(TtimeoutTechlogEvent.class));
    }

    @Test
    void createEvent_ShouldSucceedForAllImplementedConcreteEventTypes() {
        TechlogEventFactory factory = new TechlogEventFactory("cluster", "server");
        AbstractTechlogEvent event;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("_groupname", "grouname");
        parameters.put("_servername", "server");
        parameters.put("_processId", "processId");
        parameters.put("_fileId", "fileID");

        parameters.put("_linenumber", "1");
        parameters.put("_timestamp", "00:15");
        parameters.put("_timepart", "364000");
        parameters.put("_duration", "2172000");

        parameters.put("_level", "1");

        parameters.put("_type", "CONTEXT");
        event = factory.createEvent(parameters, null);
        assertThat(event, is(instanceOf(ContextTechlogEvent.class)));

        parameters.put("_type", "TDEADLOCK");
        event = factory.createEvent(parameters, null);
        assertThat(event, is(instanceOf(TdeadlockTechlogEvent.class)));

        parameters.put("_type", "TLOCK");
        event = factory.createEvent(parameters, null);
        assertThat(event, is(instanceOf(TlockTechlogEvent.class)));

        parameters.put("_type", "TTIMEOUT");
        event = factory.createEvent(parameters, null);
        assertThat(event, is(instanceOf(TtimeoutTechlogEvent.class)));
    }

}
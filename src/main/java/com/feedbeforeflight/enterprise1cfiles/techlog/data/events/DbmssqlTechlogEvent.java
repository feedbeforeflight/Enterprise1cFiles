package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;

public class DbmssqlTechlogEvent extends AbstractTechlogEvent {

    protected Short transaction;
    protected Integer dbPid;
    protected String sql;
    protected String planSqlText;

    public DbmssqlTechlogEvent() {
        super(TechlogEventType.DBMSSQL);
    }

}

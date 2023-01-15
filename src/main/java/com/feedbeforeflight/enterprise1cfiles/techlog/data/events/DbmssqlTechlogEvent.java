package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DbmssqlTechlogEvent extends AbstractTechlogEvent {

    protected Short transaction;
    protected Integer dbPid;
    protected String sql;
    protected String planSqlText;

    public static TechlogEventType classType() {
        return TechlogEventType.DBMSSQL;
    }

    @Override
    public TechlogEventType getType() {
        return classType();
    }

}

package com.feedbeforeflight.enterprise1cfiles.techlog.mapper.mappers;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.events.DbmssqlTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.mapper.AbstractTechlogEventFieldMapper;

import java.util.Map;

public class DbmssqlTechlogEventFieldMapper extends AbstractTechlogEventFieldMapper {

    @Override
    protected void mapSpecificFields(AbstractTechlogEvent event, Map<String, String> parameters, AbstractTechlogEvent prevEvent) {
        DbmssqlTechlogEvent specificEvent = (DbmssqlTechlogEvent) event;

        parameters.forEach((key, value) -> {
            switch (key) {
                case "Trans" -> specificEvent.setTransaction(Short.valueOf(removeQuotes(value)));
                case "dbpid" -> specificEvent.setDbPid(Integer.valueOf(removeQuotes(value)));
                case "sql" -> specificEvent.setSql(removeQuotes(value));
                case "planSQLText" -> specificEvent.setPlanSqlText(removeQuotes(value));
            }
        });
    }
}

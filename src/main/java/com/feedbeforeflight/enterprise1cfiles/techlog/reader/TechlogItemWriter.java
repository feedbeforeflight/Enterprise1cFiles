package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;

public interface TechlogItemWriter {

    void writeItem(AbstractTechlogEvent event);

    int getLinesLoaded(String fileId);

    void loadFinished(String fileId);

}

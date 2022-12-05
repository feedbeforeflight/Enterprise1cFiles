package com.feedbeforeflight.enterprise1cfiles.techlog.data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TechlogItemWriter {

    void writeItem(AbstractTechlogEvent event);

//    int getLinesLoaded(String fileId);

    Map<String, Instant> getLastProgressBatch(List<String> fileIds);

    void loadFinished(String fileId);

}

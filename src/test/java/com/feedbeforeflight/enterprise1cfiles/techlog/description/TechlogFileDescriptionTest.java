package com.feedbeforeflight.enterprise1cfiles.techlog.description;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogFileDescriptionTest {

    private Path getTestPath() {
        return getTestPath("22122311");
    }

    private Path getTestPath(String fileName) {
        return Paths.get("c:\\temp\\rphost_4188\\" + fileName + ".log");
    }

    @Test
    void createFileId_ShouldMakeCorrectId() {
        String fileId = TechlogFileDescription.createFileId(getTestPath(), TechlogProcessType.RPHOST, 4188);

        assertThat(fileId, equalTo("rphost_4188_22122311"));
    }

    @Test
    void compareTo_ShouldCorrectlyCompareDescriptionsByTimestamp() {
        TechlogFileDescription description1 = new TechlogFileDescription(getTestPath("22122312"),
                TechlogProcessType.RPHOST, 4188, "main_group", "test_server", null);
        TechlogFileDescription description2 = new TechlogFileDescription(getTestPath("22122313"),
                TechlogProcessType.RPHOST, 4188, "main_group", "test_server", null);
        TechlogFileDescription description3 = new TechlogFileDescription(getTestPath("22122411"),
                TechlogProcessType.RPHOST, 4188, "main_group", "test_server", null);
        TechlogFileDescription description4 = new TechlogFileDescription(getTestPath("22122411"),
                TechlogProcessType.RPHOST, 3254, "main_group", "test_server", null);

        assertThat(description1.compareTo(description2), lessThan(0));
        assertThat(description1.compareTo(description3), lessThan(0));
        assertThat(description3.compareTo(description2), greaterThan(0));
        assertThat(description3.compareTo(description4), equalTo(0));
    }
    @Test
    void updateLastRead_ShouldSetLastReadTimestamtToCurrentTime() {
        TechlogFileDescription description = new TechlogFileDescription(getTestPath("22122312"),
                TechlogProcessType.RPHOST, 4188, "main_group", "test_server", null);

        assertThat(description.getLastRead(), nullValue());

        Date marker = new Date();
        description.updateLastRead();
        assertThat(description.getLastRead(), notNullValue());
        assertThat(description.getLastRead(), greaterThanOrEqualTo(marker));
    }

}
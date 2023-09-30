package com.feedbeforeflight.enterprise1cfiles.techlog.source;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogTest {

    @Test
    void refresh_ShouldLoad(@TempDir Path tempPath) throws IOException {

        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);
        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        Techlog techlog = new Techlog(tempPath.toString(),
                "main_group", "test_server", writer);
        techlog.refresh();

        assertThat(techlog.size(), equalTo(3));
        assertThat(techlog.get(directoryPath1.getFileName().toString()).getProcessId(), equalTo(4188));
        assertThat(techlog.get(directoryPath2.getFileName().toString()).getProcessId(), equalTo(5132));
        assertThat(techlog.get(directoryPath3.getFileName().toString()).getProcessId(), equalTo(7400));
        assertThat(techlog.get(directoryPath1.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlog.get(directoryPath2.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlog.get(directoryPath3.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));

    }

    @Test
    void refresh_ShouldAppendNewlyCreatedDirectories(@TempDir Path tempPath) throws IOException {
        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        Techlog techlog = new Techlog(tempPath.toString(),
                "main_group", "test_server", writer);
        techlog.refresh();

        assertThat(techlog.size(), equalTo(2));
        assertThat(techlog.get(directoryPath1.getFileName().toString()).getProcessId(), equalTo(4188));
        assertThat(techlog.get(directoryPath2.getFileName().toString()).getProcessId(), equalTo(5132));
        assertThat(techlog.get(directoryPath1.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlog.get(directoryPath2.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));

        TechlogDirectory techlogDirectory1 = techlog.get(directoryPath1.getFileName().toString());
        TechlogDirectory techlogDirectory2 = techlog.get(directoryPath2.getFileName().toString());

        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        techlog.refresh();

        assertThat(techlog.size(), equalTo(3));
        assertThat(techlog.get(directoryPath1.getFileName().toString()), sameInstance(techlogDirectory1));
        assertThat(techlog.get(directoryPath2.getFileName().toString()), sameInstance(techlogDirectory2));
        assertThat(techlog.get(directoryPath3.getFileName().toString()).getProcessId(), equalTo(7400));
        assertThat(techlog.get(directoryPath3.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
    }

    @Test
    void refresh_ShouldRemoveDeletedDirectories(@TempDir Path tempPath) throws IOException {

        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);
        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        Techlog techlog = new Techlog(tempPath.toString(),
                "main_group", "test_server", writer);
        techlog.refresh();

        assertThat(techlog.size(), equalTo(3));

        TechlogDirectory techlogDirectory1 = techlog.get(directoryPath1.getFileName().toString());
        TechlogDirectory techlogDirectory3 = techlog.get(directoryPath3.getFileName().toString());

        Files.delete(directoryPath2);

        techlog.refresh();

        assertThat(techlog.size(), equalTo(2));
        assertThat(techlog.get(directoryPath1.getFileName().toString()), sameInstance(techlogDirectory1));
        assertThat(techlog.get(directoryPath3.getFileName().toString()), sameInstance(techlogDirectory3));
    }

}
package com.feedbeforeflight.enterprise1cfiles.techlog.description;

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

class TechlogDescriptionTest {

    @Test
    void readFileDescriptions_ShouldLoad(@TempDir Path tempPath) throws IOException {

        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);
        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        TechlogDescription techlogDescription = new TechlogDescription(tempPath.toString(),
                "main_group", "test_server", writer);
        techlogDescription.readFileDescriptions();

        assertThat(techlogDescription.size(), equalTo(3));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()).getProcessId(), equalTo(4188));
        assertThat(techlogDescription.get(directoryPath2.getFileName().toString()).getProcessId(), equalTo(5132));
        assertThat(techlogDescription.get(directoryPath3.getFileName().toString()).getProcessId(), equalTo(7400));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlogDescription.get(directoryPath2.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlogDescription.get(directoryPath3.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));

    }

    @Test
    void readLogfileDescriptions_ShouldAppendNewlyCreatedDirectories(@TempDir Path tempPath) throws IOException {
        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        TechlogDescription techlogDescription = new TechlogDescription(tempPath.toString(),
                "main_group", "test_server", writer);
        techlogDescription.readFileDescriptions();

        assertThat(techlogDescription.size(), equalTo(2));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()).getProcessId(), equalTo(4188));
        assertThat(techlogDescription.get(directoryPath2.getFileName().toString()).getProcessId(), equalTo(5132));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(techlogDescription.get(directoryPath2.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));

        TechlogDirectoryDescription directoryDescription1 = techlogDescription.get(directoryPath1.getFileName().toString());
        TechlogDirectoryDescription directoryDescription2 = techlogDescription.get(directoryPath2.getFileName().toString());

        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        techlogDescription.readFileDescriptions();

        assertThat(techlogDescription.size(), equalTo(3));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()), sameInstance(directoryDescription1));
        assertThat(techlogDescription.get(directoryPath2.getFileName().toString()), sameInstance(directoryDescription2));
        assertThat(techlogDescription.get(directoryPath3.getFileName().toString()).getProcessId(), equalTo(7400));
        assertThat(techlogDescription.get(directoryPath3.getFileName().toString()).getProcessType(), equalTo(TechlogProcessType.RPHOST));
    }

    @Test
    void readLogfileDescriptions_ShouldRemoveDeletedDirectories(@TempDir Path tempPath) throws IOException {

        Path directoryPath1 = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath1);
        Path directoryPath2 = Paths.get(tempPath.toString(), "rphost_5132");
        Files.createDirectory(directoryPath2);
        Path directoryPath3 = Paths.get(tempPath.toString(), "rphost_7400");
        Files.createDirectory(directoryPath3);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        TechlogDescription techlogDescription = new TechlogDescription(tempPath.toString(),
                "main_group", "test_server", writer);
        techlogDescription.readFileDescriptions();

        assertThat(techlogDescription.size(), equalTo(3));

        TechlogDirectoryDescription directoryDescription1 = techlogDescription.get(directoryPath1.getFileName().toString());
        TechlogDirectoryDescription directoryDescription3 = techlogDescription.get(directoryPath3.getFileName().toString());

        Files.delete(directoryPath2);

        techlogDescription.readFileDescriptions();

        assertThat(techlogDescription.size(), equalTo(2));
        assertThat(techlogDescription.get(directoryPath1.getFileName().toString()), sameInstance(directoryDescription1));
        assertThat(techlogDescription.get(directoryPath3.getFileName().toString()), sameInstance(directoryDescription3));
    }

}
package com.feedbeforeflight.enterprise1cfiles.techlog.source;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogDirectoryTest {

    private String createAndFillLogfile(Path directoryPath, String name, String content, int processId) throws IOException {
        Path filePath = Paths.get(directoryPath.toString(), name);
        Files.writeString(filePath, "\ufeff"); //  0xEF,0xBB,0xBF
        if (content != null && !content.isEmpty()) {
            Files.writeString(filePath, content);
        }
        return TechlogFile.createFileId(filePath, TechlogProcessType.RPHOST, processId);
    }

    private void deleteLogFile(Path directoryPath, String name) throws IOException {
        Path filePath = Paths.get(directoryPath.toString(), name);
        Files.delete(filePath);
    }

    @Test
    void refreshLogFiles_ShouldLoad(@TempDir Path tempPath) throws IOException {

        Path directoryPath = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath);
        createAndFillLogfile(directoryPath, "22122314.log", "03:54.025014-0,EXCP,2,process=rphost", 4188);
        createAndFillLogfile(directoryPath, "22122315.log", null, 4188);
        String fileId3 = createAndFillLogfile(directoryPath, "22122312.log", "03:53.935004-0,EXCP,2,process=rphost", 4188);
        createAndFillLogfile(directoryPath, "22122313.log", "52:52.971015-0,EXCP,2,process=rphost", 4188);

        Instant referenceLastRecordTimestamp = Instant.now();
        Map<String, Instant> lastProgressBatchResult = new HashMap<>();
        lastProgressBatchResult.put(fileId3, referenceLastRecordTimestamp);

        TechlogItemWriter writer = Mockito.spy(TechlogItemWriter.class);
        Mockito.when(writer.getLastProgressBatch(Mockito.anyList())).thenReturn(lastProgressBatchResult);

        TechlogDirectory techlogDirectory = new TechlogDirectory(directoryPath,
                "main_group", "test_server", writer);
        techlogDirectory.refreshLogFiles();

        ArgumentCaptor<List<String>> progressBatchCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(writer, Mockito.times(1)).getLastProgressBatch(progressBatchCaptor.capture());
        List<String> idList = progressBatchCaptor.getValue();
        assertThat(idList, hasSize(3)); // file #2 ignored as it contains only BOM
        assertThat(techlogDirectory.getFileById(fileId3).getLastLoadedEventTimestamp(), equalTo(referenceLastRecordTimestamp));
    }

    @Test
    void refreshLogFiles_ShouldAppendNewlyCreatedFiles(@TempDir Path tempPath) throws IOException {

        Path directoryPath = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath);
        String fileId1 = createAndFillLogfile(directoryPath, "22122314.log", "03:54.025014-0,EXCP,2,process=rphost", 4188);
        createAndFillLogfile(directoryPath, "22122315.log", null, 4188);
        String fileId3 = createAndFillLogfile(directoryPath, "22122312.log", "03:53.935004-0,EXCP,2,process=rphost", 4188);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);

        TechlogDirectory techlogDirectory = new TechlogDirectory(directoryPath,
                "main_group", "test_server", writer);
        techlogDirectory.refreshLogFiles();

        assertThat(techlogDirectory.size(), equalTo(2));

        TechlogFile techlogFile1 = techlogDirectory.getFileById(fileId1);
        TechlogFile techlogFile3 = techlogDirectory.getFileById(fileId3);

        createAndFillLogfile(directoryPath, "22122313.log", "52:52.971015-0,EXCP,2,process=rphost", 4188);
        techlogDirectory.refreshLogFiles();

        assertThat(techlogDirectory.size(), equalTo(3));
        assertThat(techlogDirectory.getFileById(fileId1), sameInstance(techlogFile1));
        assertThat(techlogDirectory.getFileById(fileId3), sameInstance(techlogFile3));
    }

    @Test
    void refreshLogFiles_ShouldRemoveDeletedFiles(@TempDir Path tempPath) throws IOException {
        Path directoryPath = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath);
        createAndFillLogfile(directoryPath, "22122314.log", "03:54.025014-0,EXCP,2,process=rphost", 4188);
        createAndFillLogfile(directoryPath, "22122315.log", null, 4188);
        String fileId3 = createAndFillLogfile(directoryPath, "22122312.log", "03:53.935004-0,EXCP,2,process=rphost", 4188);
        String fileId4 = createAndFillLogfile(directoryPath, "22122313.log", "52:52.971015-0,EXCP,2,process=rphost", 4188);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);

        TechlogDirectory techlogDirectory = new TechlogDirectory(directoryPath,
                "main_group", "test_server", writer);
        techlogDirectory.refreshLogFiles();

        assertThat(techlogDirectory.size(), equalTo(3));
        TechlogFile techlogFile3 = techlogDirectory.getFileById(fileId3);
        TechlogFile techlogFile4 = techlogDirectory.getFileById(fileId4);

        deleteLogFile(directoryPath, "22122314.log");
        techlogDirectory.refreshLogFiles();

        assertThat(techlogDirectory.size(), equalTo(2));
        assertThat(techlogDirectory.getFileById(fileId3), sameInstance(techlogFile3));
        assertThat(techlogDirectory.getFileById(fileId4), sameInstance(techlogFile4));
    }

}
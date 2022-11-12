package com.feedbeforeflight;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        if (args.length == 0) {
            return;
        }

        String command = args[0];
        if (!command.equals("-techlogfile")) {
            return;
        }

        String pathName = args[1];

//        String pathName = "d:\\Data\\techlog\\tlock\\erp-01-01\\rphost_4236\\20031406.log";
        Path path = Paths.get(pathName);
        TechlogFileDescription description = new TechlogFileDescription(path, TechlogProcessType.RPHOST, 4236, "erp-01-01",
                new TechlogItemWriter() {
                    @Override
                    public void writeItem(AbstractTechlogEvent event) {

                    }

                    @Override
                    public int getLinesLoaded(String fileId) {
                        return 0;
                    }

                    @Override
                    public void loadFinished(String fileId) {

                    }
                });
        try (TechlogFileReader reader = new TechlogFileReader(description)) {
            reader.openFile();

            AbstractTechlogEvent event = null;
            Deque<String> lines = reader.readItemLines();

            while(!lines.isEmpty()) {
                List<String> tokens = TechlogFileFieldTokenizer.readEventTokens(lines);
                Map<String, String> parameters = TechlogItemProcessor.process(tokens, description, reader.getLineNumber());
                System.out.println(parameters);
                lines = reader.readItemLines();
            }
            reader.closeFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class LogFileItemReaderTest {

    private int index = 0;
    private List<String> testData;
    String[] recordStrings = new String[3];

    @BeforeEach
    void prepareTestData() {
        index = 0;
        testData = Arrays.asList(testDataTemplate.split("\n"));

        recordStrings[0] = testData.stream().limit(5).collect(Collectors.joining());
        recordStrings[1] = testData.stream().skip(5).limit(39).collect(Collectors.joining());
        recordStrings[2] = testData.stream().skip(44).collect(Collectors.joining());
    }

    private String readLine() {
        return index >= testData.size() ? null : testData.get(index++);
    }

    @Test
    void openFile_ShouldThrowException_WhenFileNameIsEmpty() throws IOException {
        LogFileItemReader logFileItemReader = new LogFileItemReader();
        assertThrows(IllegalArgumentException.class, logFileItemReader::openFile);
    }

    @Test
    void readSeparatedLine_ShouldSucessWithTestDataProvided() throws IOException {
        LineNumberReader lineNumberReaderMock = Mockito.mock(LineNumberReader.class);
        Mockito.when(lineNumberReaderMock.readLine()).thenAnswer((Answer<String>) invocationOnMock -> readLine());

        LogFileItemReader logFileItemReader = new LogFileItemReader();
        logFileItemReader.reader = lineNumberReaderMock;

        assertEquals(recordStrings[0], logFileItemReader.readSeparatedLine());
        assertEquals(recordStrings[1], logFileItemReader.readSeparatedLine());
        assertEquals(recordStrings[2], logFileItemReader.readSeparatedLine());
    }

    @Test
    void tokenize_ShouldSuccessWithTestDataProvided() {
        LogFileItemReader logFileItemReader = new LogFileItemReader();

        List<String> tokens;

        tokens = logFileItemReader.tokenize(recordStrings[0]);
        assertEquals(19, tokens.size());
        assertEquals("20220118094400", tokens.get(0));
        assertEquals("N", tokens.get(1));
        assertEquals("{0,0}", tokens.get(2));
        assertEquals("2680", tokens.get(3));
        assertEquals("{\"U\"}", tokens.get(11));
        assertEquals("17327", tokens.get(16));
        assertEquals("{0}", tokens.get(18));

        tokens = logFileItemReader.tokenize(recordStrings[1]);
        assertEquals(19, tokens.size());
        assertEquals("20220118094400", tokens.get(0));
        assertEquals("N", tokens.get(1));
        assertEquals("{0,0}", tokens.get(2));
        assertEquals("212", tokens.get(3));
        assertTrue(tokens.get(11).contains("Иванов Петр Сергеевич"));
        assertEquals("16418", tokens.get(16));
        assertEquals("{0}", tokens.get(18));

        tokens = logFileItemReader.tokenize(recordStrings[2]);
        assertEquals(19, tokens.size());
        assertEquals("20220118094400", tokens.get(0));
        assertEquals("U", tokens.get(1));
        assertEquals("{2440ef376a050,ff9896}", tokens.get(2));
        assertEquals("2980", tokens.get(3));
        assertEquals("{\"R\",29727:8b93005056a76d7511ec78193d17e586}", tokens.get(11));
        assertEquals("16390", tokens.get(16));
        assertEquals("{0}", tokens.get(18));
    }

    private String testDataTemplate = "{20220118094400,N,\n" +
            "{0,0},2680,0,10,0,9,I,\"\",0,\n" +
            "{\"U\"},\"\",0,0,0,17327,0,\n" +
            "{0}\n" +
            "},\n" +
            "{20220118094400,N,\n" +
            "{0,0},212,54,2,1017,14,I,\"\",0,\n" +
            "{\"P\",\n" +
            "{97,\n" +
            "{\"B\",1},\n" +
            "{\"B\",0},\n" +
            "{\"B\",1},\n" +
            "{\"S\",\"Иванов Петр Сергеевич\"},\n" +
            "{\"O\",\n" +
            "{\"#\",fc01b5df-97fe-449b-83d4-218a090e681e,00000000-0000-0000-0000-000000000000}\n" +
            "},\n" +
            "{\"B\",0},\n" +
            "{\"B\",0},\n" +
            "{\"B\",0},\n" +
            "{\"S\",\"Иванов Петр Сергеевич\"},\n" +
            "{\"S\",\"\\\\Domain\\IvanovPetr\"},\n" +
            "{\"S\",\"Авто\"},\n" +
            "{\"A\",\n" +
            "{54,\n" +
            "{\"O\",\n" +
            "{\"#\",fc01b5df-97fe-449b-83d4-218a090e681e,48ec5d6b-e93d-4106-be39-b013086a6f87}\n" +
            "},\n" +
            "{\"O\",\n" +
            "{\"#\",fc01b5df-97fe-449b-83d4-218a090e681e,6a792dbe-a778-4ff5-8bb6-cd9f2c42add0}\n" +
            "},\n" +
            "{\"O\",\n" +
            "{\"#\",fc01b5df-97fe-449b-83d4-218a090e681e,8a92f91b-652a-49a8-a4f0-ddaf36410c71}\n" +
            "}\n" +
            "}\n" +
            "},\n" +
            "{\"O\",\n" +
            "{\"#\",fc01b5df-97fe-449b-83d4-218a090e681e,00000000-0000-0000-0000-000000000000}\n" +
            "},\n" +
            "{\"B\",1},\n" +
            "{\"S\",\"\"}\n" +
            "}\n" +
            "},\"\",8,36,0,16418,0,\n" +
            "{0}\n" +
            "},\n" +
            "{20220118094400,U,\n" +
            "{2440ef376a050,ff9896},2980,86,2,1436,15,I,\"\",1607,\n" +
            "{\"R\",29727:8b93005056a76d7511ec78193d17e586},\"<>\",8,41,0,16390,0,\n" +
            "{0}\n" +
            "}";

}

/* test case
{20220118094400,N,
{0,0},2680,0,10,0,9,I,"",0,
{"U"},"",0,0,0,17327,0,
{0}
},
{20220118094400,N,
{0,0},212,54,2,1017,14,I,"",0,
{"P",
{97,
{"B",1},
{"B",0},
{"B",1},
{"S","Иванов Петр Сергеевич"},
{"O",
{"#",fc01b5df-97fe-449b-83d4-218a090e681e,00000000-0000-0000-0000-000000000000}
},
{"B",0},
{"B",0},
{"B",0},
{"S","Иванов Петр Сергеевич"},
{"S","\\Domain\IvanovPetr"},
{"S","Авто"},
{"A",
{54,
{"O",
{"#",fc01b5df-97fe-449b-83d4-218a090e681e,48ec5d6b-e93d-4106-be39-b013086a6f87}
},
{"O",
{"#",fc01b5df-97fe-449b-83d4-218a090e681e,6a792dbe-a778-4ff5-8bb6-cd9f2c42add0}
},
{"O",
{"#",fc01b5df-97fe-449b-83d4-218a090e681e,8a92f91b-652a-49a8-a4f0-ddaf36410c71}
}
}
},
{"O",
{"#",fc01b5df-97fe-449b-83d4-218a090e681e,00000000-0000-0000-0000-000000000000}
},
{"B",1},
{"S",""}
}
},"",8,36,0,16418,0,
{0}
},
{20220118094400,U,
{2440ef376a050,ff9896},2980,86,2,1436,15,I,"",1607,
{"R",29727:8b93005056a76d7511ec78193d17e586},"<>",8,41,0,16390,0,
{0}
}

*/

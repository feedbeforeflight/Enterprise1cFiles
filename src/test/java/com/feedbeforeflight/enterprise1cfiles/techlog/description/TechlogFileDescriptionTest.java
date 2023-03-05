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
import java.nio.file.StandardOpenOption;
import java.time.Instant;
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
    void updateLastRead_ShouldSetLastReadTimestampToCurrentTime() {
        TechlogFileDescription description = new TechlogFileDescription(getTestPath("22122312"),
                TechlogProcessType.RPHOST, 4188, "main_group", "test_server", null);

        assertThat(description.getLastRead(), nullValue());

        Instant marker = Instant.now();
        description.updateLastRead(Instant.now());
        assertThat(description.getLastRead(), notNullValue());
        assertThat(description.getLastRead(), greaterThanOrEqualTo(marker));
    }

    @Test
    void fileWasModified_ShouldDetectFileModificationDateChange(@TempDir Path tempPath) throws IOException {
        Path directoryPath = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath);
        Path filePath = createAndFillLogfile(directoryPath, "22122315.log", fileContent[0], 4188);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        TechlogFileDescription description = new TechlogFileDescription(filePath, TechlogProcessType.RPHOST,
                4188, "main_group", "test_server", writer);
        assertThat("File expected to be modified", description.modifiedSinceLoad());
        description.updateLastRead(Instant.now());
        assertThat("File expected to be not modified", !description.modifiedSinceLoad());
        Files.writeString(filePath, fileContent[1], StandardOpenOption.APPEND);
        assertThat("File expected to be modified", description.modifiedSinceLoad());
    }

    private Path createAndFillLogfile(Path directoryPath, String name, String content, int processId) throws IOException {
        Path filePath = Paths.get(directoryPath.toString(), name);
        Files.writeString(filePath, "\ufeff"); //  0xEF,0xBB,0xBF
        if (content != null && !content.isEmpty()) {
            Files.writeString(filePath, content);
        }
        return filePath;
    }
    
    String[] fileContent = new String[]{
            "02:01.596016-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=4232,t:clientID=2505,t:applicationName=1CV8,t:computerName=rds-01-01,t:connectID=5427,SessionID=13955,Usr=Петров Петр Петрович,Txt=Transaction lock - request. Lock space Document30456.REFLOCK.,Regions=Document30456.REFLOCK,Locks='Document30456.REFLOCK Exclusive ID=30456:88f5005056874e2011ea62965ce86463',WaitConnections=,Context=Форма.Записать : Документ.мтоКорректировкаГКПЗ.Форма.ФормаДокумента\n",
            "02:04.314019-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=4232,t:clientID=2505,t:applicationName=1CV8,t:computerName=rds-01-01,t:connectID=5427,SessionID=13955,Usr=Петров Петр Петрович,Txt=Transaction lock - request. Lock space InfoRg20642.DIMS.,Regions=InfoRg20642.DIMS,Locks='InfoRg20642.DIMS Shared Fld20643=\"мтоКорректировкаГКПЗ\"',WaitConnections=,Context='Форма.Записать : Документ.мтоКорректировкаГКПЗ.Форма.ФормаДокумента\n" +
                    "ОбщийМодуль.ВерсионированиеОбъектов.Модуль : 6 : Если ОбъектВерсионируется(Источник, ЧислоВерсийОбъекта) Тогда\n" +
                    "\tОбщийМодуль.ВерсионированиеОбъектов.Модуль : 37 : ВариантВерсионирования = ПолучитьВариантВерсионирования(Источник);\n" +
                    "\t\tОбщийМодуль.ВерсионированиеОбъектов.Модуль : 65 : НастройкаВерсионирования.Прочитать();'\n",
            "02:04.346005-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=4232,t:clientID=2505,t:applicationName=1CV8,t:computerName=rds-01-01,t:connectID=5427,SessionID=13955,Usr=Петров Петр Петрович,Txt=Transaction lock - request. Lock space InfoRg33225.DIMS.,Regions=InfoRg33225.DIMS,Locks='InfoRg33225.DIMS Exclusive Fld33226=30456:88f5005056874e2011ea62965ce86463 Fld33227=T\"20200313000000\" Fld33228=192:8672005056874e2011e846caf28460af',WaitConnections=,Context='Форма.Записать : Документ.мтоКорректировкаГКПЗ.Форма.ФормаДокумента\n" +
                    "ОбщийМодуль.ПодпискиНаСобытия.Модуль : 2610 : Привилегированный.Документ_РегистрацияИзменений(Источник.Ссылка, Отказ);\n" +
                    "\tОбщийМодуль.Привилегированный.Модуль : 430 : Запись.Записать(Истина);'\n",
            "02:04.408019-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=4232,t:clientID=2505,t:applicationName=1CV8,t:computerName=rds-01-01,t:connectID=5427,SessionID=13955,Usr=Петров Петр Петрович,Txt=Transaction lock - request. Lock space UsersWorkHistory.HISTORYLOCK.,Regions=UsersWorkHistory.HISTORYLOCK,Locks='UsersWorkHistory.HISTORYLOCK Exclusive UserID=\"99bcf95f46f8d0bd4252bf54e9838f76\"',WaitConnections=,Context=Форма.Записать : Документ.мтоКорректировкаГКПЗ.Форма.ФормаДокумента\n",
            "02:42.548012-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=4232,t:clientID=2505,t:applicationName=1CV8,t:computerName=rds-01-01,t:connectID=5427,SessionID=13955,Usr=Петров Петр Петрович,Txt=Transaction lock - request. Lock space SystemSettings.SYSTEMLOCK.,Regions=SystemSettings.SYSTEMLOCK,Locks='SystemSettings.SYSTEMLOCK Exclusive ObjectKey=\"ИсторияВыбора\" SettingsKey=\"\" UserId=\"Петров Петр Петрович\"',WaitConnections=,Context=Данные.СинхронизироватьИсториюВыбора\n",
            "23:50.091019-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=6084,t:clientID=664,t:applicationName=1CV8,t:computerName=rds-01-02,t:connectID=2420,SessionID=11793,Usr=Иванов Иван Иванович,AppID=1CV8,Txt=Transaction lock - request. Lock space Reference235.REFLOCK.,Regions=Reference235.REFLOCK,Locks='Reference235.REFLOCK Exclusive ID=235:84e4005056874e2011e6f4d1f1e040a1',WaitConnections=\n",
            "23:50.107004-0,Context,3,process=rphost,p:processName=upp,OSThread=6084,t:clientID=664,t:applicationName=1CV8,t:computerName=rds-01-02,t:connectID=2420,SessionID=11793,Usr=Иванов Иван Иванович,AppID=1CV8,Context='\n" +
                    "Отчет.ИспользованиеЛимитовПКПоЗаявкамНаОплату.Форма.ФормаОтчета.Форма : 223 : ТиповыеОтчеты.ОбработкаЗакрытияНастройкиОтчета(ЭтотОбъект, ЭтаФорма, Отказ, СтандартнаяОбработка);\n" +
                    "\tОбщийМодуль.ТиповыеОтчеты.Модуль : 3392 : СохранитьНастройкуПользователяНастройкиОтчета(ОтчетОбъект, ФормаОтчета);\n" +
                    "\t\tОбщийМодуль.ТиповыеОтчеты.Модуль : 3443 : Настройка.Записать();'\n",
            "23:50.122003-3,TLOCK,4,process=rphost,p:processName=upp,OSThread=6084,t:clientID=664,t:applicationName=1CV8,t:computerName=rds-01-02,t:connectID=2420,SessionID=11793,Usr=Иванов Иван Иванович,AppID=1CV8,Txt=Transaction lock - request. Lock space InfoRg32933.DIMS.,Regions=InfoRg32933.DIMS,Locks='InfoRg32933.DIMS Exclusive Fld32934=235:84e4005056874e2011e6f4d1f1e040a1 Fld32935=32859:8b8e0d3f45ca3131460c235198c3bc52',WaitConnections=,Context='\n" +
                    "ОбщийМодуль.ОбменДаннымиВызовСервера.Модуль : 498 : ОбменДаннымиСервер.УдалитьНаборЗаписейВРегистреСведений(СтруктураЗаписи, \"РезультатыОбменаДанными\");\n" +
                    "\tОбщийМодуль.ОбменДаннымиСервер.Модуль : 7749 : НаборЗаписей.Записать();'\n",
            "23:50.138009-0,Context,3,process=rphost,p:processName=upp,OSThread=6084,t:clientID=664,t:applicationName=1CV8,t:computerName=rds-01-02,t:connectID=2420,SessionID=11793,Usr=Иванов Иван Иванович,AppID=1CV8,Context='\n" +
                    "Отчет.ИспользованиеЛимитовПКПоЗаявкамНаОплату.Форма.ФормаОтчета.Форма : 223 : ТиповыеОтчеты.ОбработкаЗакрытияНастройкиОтчета(ЭтотОбъект, ЭтаФорма, Отказ, СтандартнаяОбработка);\n" +
                    "\tОбщийМодуль.ТиповыеОтчеты.Модуль : 3392 : СохранитьНастройкуПользователяНастройкиОтчета(ОтчетОбъект, ФормаОтчета);\n" +
                    "\t\tОбщийМодуль.ТиповыеОтчеты.Модуль : 3443 : Настройка.Записать();\n" +
                    "\t\t\tОбщийМодуль.ОбменДаннымиСобытия.Модуль : 2977 : РегистрыСведений.РезультатыОбменаДанными.ЗарегистрироватьУстранениеПроблемы(Источник, Перечисления.ТипыПроблемОбменаДанными.НезаполненныеРеквизиты);\n" +
                    "\t\t\t\tРегистрСведений.РезультатыОбменаДанными.МодульМенеджера : 143 : ОбменДаннымиВызовСервера.ЗарегистрироватьУстранениеПроблемы(СсылкаНаИсточник, ТипПроблемы, НовоеЗначениеПометкиУдаления);'\n"
    };

}
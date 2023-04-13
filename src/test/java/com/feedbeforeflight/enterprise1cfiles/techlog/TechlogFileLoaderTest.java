package com.feedbeforeflight.enterprise1cfiles.techlog;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogItemWriter;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.description.TechlogFileDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.EnumMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogFileLoaderTest {

    private Path createAndFillLogfile(Path directoryPath, String name, String content, int processId) throws IOException {
        Path filePath = Paths.get(directoryPath.toString(), name);
        Files.writeString(filePath, "\ufeff"); //  0xEF,0xBB,0xBF
        if (content != null && !content.isEmpty()) {
            Files.writeString(filePath, content);
        }
        return filePath;
    }

    private void deleteLogFile(Path directoryPath, String name) throws IOException {
        Path filePath = Paths.get(directoryPath.toString(), name);
        Files.delete(filePath);
    }

    @Test
    void loadFile_shouldLoadNewLinesOnly(@TempDir Path tempPath) throws IOException {

        Instant marker = Instant.now();

        Path directoryPath = Paths.get(tempPath.toString(), "rphost_4188");
        Files.createDirectory(directoryPath);
        Path filePath = createAndFillLogfile(directoryPath, "22122315.log", fileContent[0], 4188);
        Files.writeString(filePath, fileContent[1], StandardOpenOption.APPEND);

        TechlogItemWriter writer = Mockito.mock(TechlogItemWriter.class);
        TechlogFileDescription description = new TechlogFileDescription(filePath, TechlogProcessType.RPHOST,
                4188, "main_group", "test_server");
        EnumMap<TechlogEventType, Integer> stats = TechlogFileLoader.load(writer, description);

        assertThat(stats.size(), equalTo(1));
        assertThat(stats.get(TechlogEventType.TLOCK), equalTo(2));
        assertThat(description.getLastRead(), greaterThanOrEqualTo(marker));
        assertThat(description.getLinesRead(), equalTo(5));

        marker = Instant.now();
        Files.writeString(filePath, fileContent[2], StandardOpenOption.APPEND);
        stats = TechlogFileLoader.load(writer, description);

        assertThat(stats.size(), equalTo(1));
        assertThat(stats.get(TechlogEventType.TLOCK), equalTo(1));
        assertThat(description.getLastRead(), greaterThanOrEqualTo(marker));
        assertThat(description.getLinesRead(), equalTo(8));
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
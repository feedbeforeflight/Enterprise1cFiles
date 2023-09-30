package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Deque;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogFileReaderTest {

    @Test
    void readItemLines_ShouldSucceed_WithExtraEmptyLineAtEnd() throws NoSuchFieldException, IllegalAccessException, IOException {
        TechlogFile techlogFile = Mockito.mock(TechlogFile.class);

        TechlogFileReader reader = new TechlogFileReader(techlogFile);
        LineNumberReader lineNumberReader = Mockito.mock(LineNumberReader.class);
        Mockito.when(lineNumberReader.readLine()).
                thenReturn(
                        "ОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 281 : ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания(Метаданные.РегламентныеЗадания.ОбновлениеИндексаППД);",
                        "\tОбщийМодуль.ОбщегоНазначения.Модуль : 5016 : Справочники.ВерсииРасширений.ЗарегистрироватьИспользованиеВерсииРасширений();",
                        "\t\tСправочник.ВерсииРасширений.МодульМенеджера : 173 : Блокировка.Заблокировать();'",

                        "59:58.589020-3,TLOCK,4,process=rphost,p:processName=zup_besk,OSThread=6856,t:clientID=2184,t:applicationName=BackgroundJob,t:computerName=erp-01-01.bashes.ru,t:connectID=25522,SessionID=15,Usr=DefUser,Regions=Reference42.REFLOCK,Locks='Reference42.REFLOCK Shared Fld1091=0',WaitConnections=,Context='",
                        "ОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 287 : ОбновлениеИндексаППД();",
                        "\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 14 : ОбновитьИндекс(НСтр(\"ru = ''Обновление индекса ППД''\"), Ложь, Истина);",
                        "\t\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 582 : ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания();",
                        "\t\t\tОбщийМодуль.ОбщегоНазначения.Модуль : 5016 : Справочники.ВерсииРасширений.ЗарегистрироватьИспользованиеВерсииРасширений();",
                        "\t\t\t\tСправочник.ВерсииРасширений.МодульМенеджера : 173 : Блокировка.Заблокировать();'",
                        "",
                        null);

        Field readerField = reader.getClass().getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(reader, lineNumberReader);

        Field recordStartLineBufferField = reader.getClass().getDeclaredField("recordStartLineBuffer");
        recordStartLineBufferField.setAccessible(true);
        recordStartLineBufferField.set(reader, "59:58.542039-3,TLOCK,4,process=rphost,p:processName=zup_besk,OSThread=6856,t:clientID=2184,t:applicationName=BackgroundJob,t:computerName=erp-01-01.bashes.ru,t:connectID=25522,SessionID=15,Usr=DefUser,Regions=Reference42.REFLOCK,Locks='Reference42.REFLOCK Shared Fld1091=0',WaitConnections=,Context='");

        Deque<String> lines = reader.readItemLines();
        assertThat(lines, hasSize(4));

        lines = reader.readItemLines();
        assertThat(lines, hasSize(6));

        lines = reader.readItemLines();
        assertThat(lines, empty());

    }

    @Test
    void readItemLines_ShouldSucceed_WithoutExtraEmptyLineAtEnd() throws NoSuchFieldException, IllegalAccessException, IOException {
        TechlogFile techlogFile = Mockito.mock(TechlogFile.class);

        TechlogFileReader reader = new TechlogFileReader(techlogFile);

        Field recordStartLineBufferField = reader.getClass().getDeclaredField("recordStartLineBuffer");
        recordStartLineBufferField.setAccessible(true);
        recordStartLineBufferField.set(reader, "59:58.542039-3,TLOCK,4,process=rphost,p:processName=zup_besk,OSThread=6856,t:clientID=2184,t:applicationName=BackgroundJob,t:computerName=erp-01-01.bashes.ru,t:connectID=25522,SessionID=15,Usr=DefUser,Regions=Reference42.REFLOCK,Locks='Reference42.REFLOCK Shared Fld1091=0',WaitConnections=,Context='");

        LineNumberReader lineNumberReader = Mockito.mock(LineNumberReader.class);
        Mockito.when(lineNumberReader.readLine()).
                thenReturn(
                        "ОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 281 : ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания(Метаданные.РегламентныеЗадания.ОбновлениеИндексаППД);",
                        "\tОбщийМодуль.ОбщегоНазначения.Модуль : 5016 : Справочники.ВерсииРасширений.ЗарегистрироватьИспользованиеВерсииРасширений();",
                        "\t\tСправочник.ВерсииРасширений.МодульМенеджера : 173 : Блокировка.Заблокировать();'",

                        "59:58.589020-3,TLOCK,4,process=rphost,p:processName=zup_besk,OSThread=6856,t:clientID=2184,t:applicationName=BackgroundJob,t:computerName=erp-01-01.bashes.ru,t:connectID=25522,SessionID=15,Usr=DefUser,Regions=Reference42.REFLOCK,Locks='Reference42.REFLOCK Shared Fld1091=0',WaitConnections=,Context='",
                        "ОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 287 : ОбновлениеИндексаППД();",
                        "\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 14 : ОбновитьИндекс(НСтр(\"ru = ''Обновление индекса ППД''\"), Ложь, Истина);",
                        "\t\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 582 : ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания();",
                        "\t\t\tОбщийМодуль.ОбщегоНазначения.Модуль : 5016 : Справочники.ВерсииРасширений.ЗарегистрироватьИспользованиеВерсииРасширений();",
                        "\t\t\t\tСправочник.ВерсииРасширений.МодульМенеджера : 173 : Блокировка.Заблокировать();'",
                        null);

        Field readerField = reader.getClass().getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(reader, lineNumberReader);

        Deque<String> lines = reader.readItemLines();
        assertThat(lines, hasSize(4));

        lines = reader.readItemLines();
        assertThat(lines, hasSize(6));

        lines = reader.readItemLines();
        assertThat(lines, empty());

    }

    @Test
    void readItemLines_ShouldSucceed_WithEmptyLinesInTextFields() throws NoSuchFieldException, IllegalAccessException, IOException {
        TechlogFile techlogFile = Mockito.mock(TechlogFile.class);
        TechlogFileReader reader = new TechlogFileReader(techlogFile);
        LineNumberReader lineNumberReader = new LineNumberReader(new StringReader(errorText));

        Field recordStartLineBufferField = reader.getClass().getDeclaredField("recordStartLineBuffer");
        recordStartLineBufferField.setAccessible(true);
        recordStartLineBufferField.set(reader, lineNumberReader.readLine());

        Field readerField = reader.getClass().getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(reader, lineNumberReader);

        Deque<String> lines = reader.readItemLines();
        assertThat(lines, hasSize(11));

        lines = reader.readItemLines();
        assertThat(lines, hasSize(1));

        lines = reader.readItemLines();
        assertThat(lines, empty());
    }

    String errorText = """
    02:53.578000-0,EXCP,2,process=rphost,p:processName=zup,OSThread=9776,t:clientID=9080,t:applicationName=1CV8C,t:computerName=PetrovPP,t:connectID=23141,SessionID=9284,Usr=Петров Петр Петрович,AppID=1CV8C,Exception=580392e6-ba49-4280-ac67-fcd6f2180121,Descr='src\\VResourceInfoBaseImpl.cpp(1130):
    580392e6-ba49-4280-ac67-fcd6f2180121: Неспецифицированная ошибка работы с ресурсом
    Ошибка при выполнении запроса POST к ресурсу /e1cib/logForm:
    8d366056-4d5a-4d88-a207-0ae535b7d28e: Ошибка при записи внутреннего документа:
    Поле "Подписал" не заполнено
                
    Ссылка на объект в ДО: e1cib/data/Справочник.ВнутренниеДокументы?ref=00000000000000000000000000000000
    Ссылка на объект в ИС: e1cib/data/Документ.Скан?ref=8df6005056a76d7511ee5c5c1d609135
    {ОбщийМодуль.ИнтеграцияС1СДокументооборот.Модуль(476)}:		ВызватьИсключение
    {Обработка.ИнтеграцияС1СДокументооборот.Форма.ВнутреннийДокумент.Форма(4889)}:		ИнтеграцияС1СДокументооборот.ПроверитьВозвратВебСервиса(Прокси, Результат);
    '
    34:30.627001-30993,TLOCK,4,process=rphost,p:processName=zup,OSThread=10616,t:clientID=625,t:applicationName=1CV8C,t:computerName=IvanovII,t:connectID=5731,SessionID=496,Usr=Иванов Иван Иванович,AppID=1CV8C,DBMS=DBMSSQL,DataBase=sql1c\\zup,Regions=AccumRg31253.DIMS,Locks='AccumRg31253.DIMS Exclusive Fld1048=0 Period=[T"20230901000000":+] Splitter=0 Fld31254=170:8106005056a72d3d11e82dc28e377df6 Fld31255=372:8acf005056a76d7511ebda1ed4b35f7a Fld31256=771:be057e1f8d79cacb43da2058feef7cdb',WaitConnections=,Context=Данные.ОтменитьПроведение
    
    """;

}
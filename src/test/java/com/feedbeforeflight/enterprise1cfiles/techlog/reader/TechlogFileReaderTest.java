package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.LineNumberReader;
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

}
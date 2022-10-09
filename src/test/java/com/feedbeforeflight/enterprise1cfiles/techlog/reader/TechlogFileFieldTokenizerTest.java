package com.feedbeforeflight.enterprise1cfiles.techlog.reader;

import org.junit.jupiter.api.Test;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TechlogFileFieldTokenizerTest {

    @Test
    void readEventTokens() {

        Deque<String> lines = new LinkedList<>();
        lines.add("59:58.589020-3,TLOCK,4,process=rphost,p:processName=zup_besk,OSThread=6856,t:clientID=2184,t:applicationName=BackgroundJob,t:computerName=erp-01-01.bashes.ru,t:connectID=25522,SessionID=15,Usr=DefUser,Regions=Reference42.REFLOCK,Locks='Reference42.REFLOCK Shared Fld1091=0',WaitConnections=,Context='");
        lines.add("ОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 287 : ОбновлениеИндексаППД();");
        lines.add("\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 14 : ОбновитьИндекс(НСтр(\"ru = ''Обновление индекса ППД''\"), Ложь, Истина);");
        lines.add("\t\tОбщийМодуль.ПолнотекстовыйПоискСервер.Модуль : 582 : ОбщегоНазначения.ПриНачалеВыполненияРегламентногоЗадания();");
        lines.add("\t\t\tОбщийМодуль.ОбщегоНазначения.Модуль : 5016 : Справочники.ВерсииРасширений.ЗарегистрироватьИспользованиеВерсииРасширений();");
        lines.add("\t\t\t\tСправочник.ВерсииРасширений.МодульМенеджера : 173 : Блокировка.Заблокировать();'");

        List<String> tokens = TechlogFileFieldTokenizer.readEventTokens(lines);

        assertThat(tokens, hasSize(16));
        //System.out.println(tokens.get(15));
    }
}
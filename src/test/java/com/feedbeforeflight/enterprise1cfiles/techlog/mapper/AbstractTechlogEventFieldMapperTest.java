package com.feedbeforeflight.enterprise1cfiles.techlog.mapper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


class AbstractTechlogEventFieldMapperTest {

    @Test
    void trimLeadingNewlineShouldDoIt() {
        String source = "\n" +
                "ОбщийМодуль.ЦентрМониторингаСлужебный.Модуль : 283 : ЗаписатьИнформацияКлиента(Параметры);\n" +
                "\tОбщийМодуль.ЦентрМониторингаСлужебный.Модуль : 3038 : ЦентрМониторинга.ЗаписатьОперациюБизнесСтатистикиЧас(ИмяОперацииСтатистики, ХешПользователя, Значение);\n" +
                "\t\tОбщийМодуль.ЦентрМониторинга.Модуль : 106 : ЦентрМониторингаСлужебный.ЗаписатьОперациюБизнесСтатистикиСлужебная(ПараметрыЗаписи);";
        String reference = "ОбщийМодуль.ЦентрМониторингаСлужебный.Модуль : 283 : ЗаписатьИнформацияКлиента(Параметры);\n" +
                "\tОбщийМодуль.ЦентрМониторингаСлужебный.Модуль : 3038 : ЦентрМониторинга.ЗаписатьОперациюБизнесСтатистикиЧас(ИмяОперацииСтатистики, ХешПользователя, Значение);\n" +
                "\t\tОбщийМодуль.ЦентрМониторинга.Модуль : 106 : ЦентрМониторингаСлужебный.ЗаписатьОперациюБизнесСтатистикиСлужебная(ПараметрыЗаписи);";

        String result = AbstractTechlogEventFieldMapper.trimLeadingNewline(source);
        assertThat(result, equalTo(reference));
    }
}
package com.feedbeforeflight.enterprise1cfiles.techlog.data.events;

import com.feedbeforeflight.enterprise1cfiles.techlog.data.AbstractTechlogEvent;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogEventType;
import com.feedbeforeflight.enterprise1cfiles.techlog.data.TechlogProcessType;
import com.feedbeforeflight.enterprise1cfiles.techlog.source.TechlogFile;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogEventFactory;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileFieldTokenizer;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogFileReader;
import com.feedbeforeflight.enterprise1cfiles.techlog.reader.TechlogItemProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DbmssqlTechlogEventTest {

    @Test
    void ShoulSucceed_WhenLoadingEventText() throws NoSuchFieldException, IllegalAccessException, IOException {
        Queue<String> samplesQueue = getSampleTechLogEntry(sampleWithPlanSQL);

        TechlogFile techlogFile = Mockito.mock(TechlogFile.class);
        Mockito.when(techlogFile.getHourString()).thenReturn("22122615");

        TechlogFileReader reader = new TechlogFileReader(techlogFile);

        Field recordStartLineBufferField = reader.getClass().getDeclaredField("recordStartLineBuffer");
        recordStartLineBufferField.setAccessible(true);
        recordStartLineBufferField.set(reader, samplesQueue.poll());

        LineNumberReader lineNumberReader = Mockito.mock(LineNumberReader.class);
        Mockito.when(lineNumberReader.readLine()).thenAnswer(invocation -> samplesQueue.poll());

        Field readerField = reader.getClass().getDeclaredField("reader");
        readerField.setAccessible(true);
        readerField.set(reader, lineNumberReader);

        Deque<String> lines = reader.readItemLines();
        assertThat(lines, hasSize(247));

        List<String> tokens = TechlogFileFieldTokenizer.readEventTokens(lines);
        Map<String, String> parameters = TechlogItemProcessor.process(tokens, techlogFile, reader.getLineNumber());
        TechlogEventFactory factory = new TechlogEventFactory();
        AbstractTechlogEvent event = factory.createEvent(parameters, null);

        assertThat(event.getType(), equalTo(TechlogEventType.DBMSSQL));

        DbmssqlTechlogEvent concreteEvent = (DbmssqlTechlogEvent) event;
        assertThat(concreteEvent.getProcessType(), equalTo(TechlogProcessType.RPHOST));
        assertThat(concreteEvent.getTransaction(), equalTo((short)123));
        assertThat(concreteEvent.getDbPid(), equalTo(80));
        assertThat(concreteEvent.getSql(), containsString("CASE WHEN (T9._Fld2127RRef = ?) THEN 0x01 ELSE 0x00 END,"));
        assertThat(concreteEvent.getPlanSqlText(), containsString("Sort(ORDER BY:([T1].[_IDRRef] ASC"));
        assertThat(concreteEvent.getContext(), equalTo("Система.ПолучитьФорму : Задача.УП_Задачи.Форма.ФормаСписка"));
    }

    Queue<String> getSampleTechLogEntry(String sample) {
        return Arrays.stream(sample.split("\n")).collect(Collectors.toCollection(LinkedList::new));
    }

    String sampleWithPlanSQL = "15:13.358000-62338997,DBMSSQL,5,process=rphost,p:processName=test_database,OSThread=14832,t:clientID=78192,t:applicationName=1CV8C,t:computerName=es-zosimovichis,t:connectID=31227,SessionID=3,Usr=Зосимович Игорь Сергеевич,AppID=1CV8C,DBMS=DBMSSQL,DataBase=sql05-02\\test_database,Trans=123,dbpid=80,Sql='SELECT DISTINCT TOP 45\n" +
            "T1._IDRRef,\n" +
            "T1._Marked,\n" +
            "T1._Date_Time,\n" +
            "T1._BusinessProcessRRef,\n" +
            "T1._Name,\n" +
            "T1._Executed,\n" +
            "T1._Fld426,\n" +
            "T1._Fld428RRef,\n" +
            "CASE WHEN (T9._Fld2127RRef = ?) THEN 0x01 ELSE 0x00 END,\n" +
            "CASE WHEN (T1._Fld429 = ?) THEN ? ELSE CASE WHEN (T1._Fld426 > T1._Fld429) THEN ? ELSE ? END END,\n" +
            "CASE WHEN T1._Fld431 = 0x01 AND (T1._Fld432RRef = ?) THEN ? WHEN T1._Fld431 = 0x01 AND (NOT (((T1._Fld432RRef = ?)))) THEN ? WHEN (NOT (((T10._IDRRef IS NULL)))) THEN CASE WHEN ? IN\n" +
            "(SELECT TOP 1\n" +
            "? AS Q_004_F_000_\n" +
            "FROM dbo._Task22_VT433 T13\n" +
            "WHERE ((T13._Fld315 = ?)) AND ((T13._Task22_IDRRef = T1._IDRRef) AND (T13._Fld435RRef = ?))\n" +
            "UNION ALL SELECT TOP 1\n" +
            "?\n" +
            "FROM dbo._InfoRg5102 T14\n" +
            "WHERE ((T14._Fld315 = ?)) AND ((T14._Fld5104RRef = T1._IDRRef) AND (T14._Fld5103RRef = ?))) THEN ? ELSE ? END ELSE ? END,\n" +
            "T12._Fld2720,\n" +
            "T12._Date_Time,\n" +
            "T12._Number,\n" +
            "T12._Fld2737RRef,\n" +
            "T12._Fld2761,\n" +
            "T12._Fld2740,\n" +
            "T12._Fld2748RRef,\n" +
            "T12._Fld2750RRef\n" +
            "FROM dbo._Task22 T1\n" +
            "LEFT OUTER JOIN dbo._InfoRg5102 T2\n" +
            "LEFT OUTER JOIN dbo._Task22 T3\n" +
            "ON (T2._Fld5104RRef = T3._IDRRef) AND (T3._Fld315 = ?)\n" +
            "LEFT OUTER JOIN dbo._Reference129 T4\n" +
            "ON (T3._Fld422RRef = T4._IDRRef) AND (T4._Fld315 = ?)\n" +
            "LEFT OUTER JOIN (SELECT\n" +
            "T8._Fld4962RRef AS Fld4962RRef,\n" +
            "T8._Fld4963RRef AS Fld4963RRef,\n" +
            "T8._Fld4965 AS Fld4965_\n" +
            "FROM (SELECT\n" +
            "T7._Fld4962RRef AS Fld4962RRef,\n" +
            "T7._Fld4963RRef AS Fld4963RRef,\n" +
            "T7._Fld4964RRef AS Fld4964RRef,\n" +
            "MAX(T7._Period) AS MAXPERIOD_\n" +
            "FROM dbo._InfoRg4961 T7\n" +
            "WHERE ((T7._Fld315 = ?)) AND (T7._Period <= ? AND T7._Active = 0x01 AND ((T7._Fld4964RRef = ?)))\n" +
            "GROUP BY T7._Fld4962RRef,\n" +
            "T7._Fld4963RRef,\n" +
            "T7._Fld4964RRef) T6\n" +
            "INNER JOIN dbo._InfoRg4961 T8\n" +
            "ON T6.Fld4962RRef = T8._Fld4962RRef AND T6.Fld4963RRef = T8._Fld4963RRef AND T6.Fld4964RRef = T8._Fld4964RRef AND T6.MAXPERIOD_ = T8._Period\n" +
            "WHERE (T8._Fld315 = ?)) T5\n" +
            "ON (T2._Fld5103RRef = T5.Fld4962RRef) AND (T5.Fld4965_ = 0x01) AND (T3._Fld422RRef = T5.Fld4963RRef) AND (NOT (((T4._IDRRef IS NULL))))\n" +
            "ON ((T2._Fld5104RRef = T1._IDRRef) AND (T2._Fld5105 <= ?) AND ((T2._Fld5106 >= ?) OR (T2._Fld5106 = ?))) AND (T2._Fld315 = ?)\n" +
            "LEFT OUTER JOIN dbo._Reference148 T9\n" +
            "ON (T1._Fld428RRef = T9._IDRRef) AND (T9._Fld315 = ?)\n" +
            "LEFT OUTER JOIN dbo._Reference129 T10\n" +
            "ON (T9._Fld2134RRef = T10._IDRRef) AND (T10._Fld315 = ?)\n" +
            "LEFT OUTER JOIN dbo._BPr19 T11\n" +
            "ON (T1._BusinessProcessRRef = T11._IDRRef) AND (T11._Fld315 = ?)\n" +
            "LEFT OUTER JOIN dbo._Document172 T12\n" +
            "ON (T11._Fld374RRef = T12._IDRRef) AND (T12._Fld315 = ?)\n" +
            "WHERE ((T1._Fld315 = ?)) AND ((EXISTS(\n" +
            "SELECT 1 \n" +
            "FROM dbo._Task22_VT433 T15\n" +
            "WHERE T1._Fld315 = T15._Fld315 AND T1._IDRRef = T15._Task22_IDRRef AND ((T15._Fld435RRef IN (?)))) OR (T2._Fld5103RRef IN (?)) OR (NOT (((T5.Fld4962RRef IS NULL)))) AND (NOT (((T5.Fld4963RRef IS NULL)))) OR EXISTS(\n" +
            "SELECT 1 \n" +
            "FROM dbo._Task22_VT433 T16\n" +
            "WHERE T1._Fld315 = T16._Fld315 AND T1._IDRRef = T16._Task22_IDRRef AND (EXISTS(SELECT\n" +
            "1\n" +
            "FROM (SELECT\n" +
            "T18.Fld5076RRef AS Q_001_F_000RRef,\n" +
            "T18.Fld5077RRef AS Q_001_F_001RRef\n" +
            "FROM (SELECT\n" +
            "T21._Period AS Period_,\n" +
            "T21._Fld5079 AS Fld5079_,\n" +
            "T21._Fld5076RRef AS Fld5076RRef,\n" +
            "T21._Fld5077RRef AS Fld5077RRef\n" +
            "FROM (SELECT\n" +
            "T20._Fld5076RRef AS Fld5076RRef,\n" +
            "T20._Fld5077RRef AS Fld5077RRef,\n" +
            "T20._Fld5078RRef AS Fld5078RRef,\n" +
            "MAX(T20._Period) AS MAXPERIOD_\n" +
            "FROM dbo._InfoRg5075 T20\n" +
            "WHERE ((T20._Fld315 = ?)) AND (T20._Period <= ? AND T20._Active = 0x01 AND ((T20._Fld5078RRef = ?)))\n" +
            "GROUP BY T20._Fld5076RRef,\n" +
            "T20._Fld5077RRef,\n" +
            "T20._Fld5078RRef) T19\n" +
            "INNER JOIN dbo._InfoRg5075 T21\n" +
            "ON T19.Fld5076RRef = T21._Fld5076RRef AND T19.Fld5077RRef = T21._Fld5077RRef AND T19.Fld5078RRef = T21._Fld5078RRef AND T19.MAXPERIOD_ = T21._Period\n" +
            "WHERE (T21._Fld315 = ?)) T18\n" +
            "WHERE (T18.Period_ <= ?) AND ((T18.Fld5079_ >= ?) OR (T18.Fld5079_ = ?))) T17\n" +
            "WHERE EXISTS(\n" +
            "SELECT 1 \n" +
            "FROM dbo._Task22_VT433 T22\n" +
            "WHERE T1._Fld315 = T22._Fld315 AND T1._IDRRef = T22._Task22_IDRRef AND ((T22._Fld435RRef = T17.Q_001_F_001RRef))) AND (T11._Fld372RRef = T17.Q_001_F_000RRef)))) OR EXISTS(\n" +
            "SELECT 1 \n" +
            "FROM dbo._Task22_VT433 T23\n" +
            "WHERE T1._Fld315 = T23._Fld315 AND T1._IDRRef = T23._Task22_IDRRef AND (T23._Fld435RRef IN\n" +
            "(SELECT\n" +
            "T24.Q_001_F_001RRef AS Q_002_F_000RRef\n" +
            "FROM (SELECT\n" +
            "T25.Fld5076RRef AS Q_001_F_000RRef,\n" +
            "T25.Fld5077RRef AS Q_001_F_001RRef\n" +
            "FROM (SELECT\n" +
            "T28._Period AS Period_,\n" +
            "T28._Fld5079 AS Fld5079_,\n" +
            "T28._Fld5076RRef AS Fld5076RRef,\n" +
            "T28._Fld5077RRef AS Fld5077RRef\n" +
            "FROM (SELECT\n" +
            "T27._Fld5076RRef AS Fld5076RRef,\n" +
            "T27._Fld5077RRef AS Fld5077RRef,\n" +
            "T27._Fld5078RRef AS Fld5078RRef,\n" +
            "MAX(T27._Period) AS MAXPERIOD_\n" +
            "FROM dbo._InfoRg5075 T27\n" +
            "WHERE ((T27._Fld315 = ?)) AND (T27._Period <= ? AND T27._Active = 0x01 AND ((T27._Fld5078RRef = ?)))\n" +
            "GROUP BY T27._Fld5076RRef,\n" +
            "T27._Fld5077RRef,\n" +
            "T27._Fld5078RRef) T26\n" +
            "INNER JOIN dbo._InfoRg5075 T28\n" +
            "ON T26.Fld5076RRef = T28._Fld5076RRef AND T26.Fld5077RRef = T28._Fld5077RRef AND T26.Fld5078RRef = T28._Fld5078RRef AND T26.MAXPERIOD_ = T28._Period\n" +
            "WHERE (T28._Fld315 = ?)) T25\n" +
            "WHERE (T25.Period_ <= ?) AND ((T25.Fld5079_ >= ?) OR (T25.Fld5079_ = ?))) T24\n" +
            "WHERE (T24.Q_001_F_000RRef = 0x00000000000000000000000000000000))))) AND (T1._Executed = 0x00) AND (T1._Marked = 0x00) AND (T1._Fld425 = 0x00) AND (NOT ((EXISTS(SELECT\n" +
            "? AS Q_003_F_000_\n" +
            "FROM dbo._InfoRg5116 T29\n" +
            "WHERE ((T29._Fld315 = ?)) AND ((T29._Fld5117RRef = T1._BusinessProcessRRef) AND (T29._Fld5118RRef = T1._IDRRef)))))) AND (T1._Fld431 = 0x01 AND (T1._Fld432RRef IN (?)) OR (T1._Fld431 = 0x00)) AND ((T1._Fld427 <= ?) OR (T1._Fld427 = ?)))\n" +
            "ORDER BY 7 DESC, 1 DESC\n" +
            "p_0: 0x9B83D79800BB63404554322204F29643\n" +
            "p_1: 20010101000000\n" +
            "p_2: 0N\n" +
            "p_3: 1N\n" +
            "p_4: 0N\n" +
            "p_5: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_6: 3N\n" +
            "p_7: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_8: 1N\n" +
            "p_9: 1N\n" +
            "p_10: 1N\n" +
            "p_11: 0N\n" +
            "p_12: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_13: 1N\n" +
            "p_14: 0N\n" +
            "p_15: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_16: 2N\n" +
            "p_17: 1N\n" +
            "p_18: 0N\n" +
            "p_19: 0N\n" +
            "p_20: 0N\n" +
            "p_21: 0N\n" +
            "p_22: 40221226151409\n" +
            "p_23: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_24: 0N\n" +
            "p_25: 40221226151409\n" +
            "p_26: 40221226151409\n" +
            "p_27: 20010101000000\n" +
            "p_28: 0N\n" +
            "p_29: 0N\n" +
            "p_30: 0N\n" +
            "p_31: 0N\n" +
            "p_32: 0N\n" +
            "p_33: 0N\n" +
            "p_34: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_35: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_36: 0N\n" +
            "p_37: 40221226151409\n" +
            "p_38: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_39: 0N\n" +
            "p_40: 40221226151409\n" +
            "p_41: 40221226000000\n" +
            "p_42: 20010101000000\n" +
            "p_43: 0N\n" +
            "p_44: 40221226151409\n" +
            "p_45: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_46: 0N\n" +
            "p_47: 40221226151409\n" +
            "p_48: 40221226000000\n" +
            "p_49: 20010101000000\n" +
            "p_50: 1N\n" +
            "p_51: 0N\n" +
            "p_52: 0x873600505687236411E8CB9AF0F3594D\n" +
            "p_53: 40221226151409\n" +
            "p_54: 20010101000000\n" +
            "',Rows=0,RowsAffected=0,planSQLText='\n" +
            "0, 1, 45, 0, 4.5E-006, 504, 14.9, 1,   |--Top(TOP EXPRESSION:((45)))\n" +
            "0, 1, 45, 0.0113, 0.23, 504, 14.9, 1,        |--Sort(DISTINCT ORDER BY:([T1].[_Fld426] DESC, [T1].[_IDRRef] DESC, [Expr1036] ASC, [Expr1037] ASC, [Expr1041] ASC, [T12].[_Fld2720] ASC, [T12].[_Date_Time] ASC, [T12].[_Number] ASC, [T12].[_Fld2737RRef] ASC, [T12].[_Fld2761] ASC, [T12].[_Fld2740] ASC, [T12].[_Fld2748RRef] ASC, [T12].[_Fld2750RRef] ASC))\n" +
            "0, 0, 6.84E+003, 0, 0.000684, 504, 14.7, 1,             |--Compute Scalar(DEFINE:([Expr1036]=CASE WHEN [test_database].[dbo].[_Reference148].[_Fld2127RRef] as [T9].[_Fld2127RRef]=[@P1] THEN 0x01 ELSE 0x00 END, [Expr1037]=CASE WHEN [test_database].[dbo].[_Task22].[_Fld429] as [T1].[_Fld429]=[@P2] THEN [@P3] ELSE CASE WHEN [test_database].[dbo].[_Task22].[_Fld426] as [T1].[_Fld426]>[test_database].[dbo].[_Task22].[_Fld429] as [T1].[_Fld429] THEN [@P4] ELSE [@P5] END END, [Expr1041]=CASE WHEN [test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x01 AND [test_database].[dbo].[_Task22].[_Fld432RRef] as [T1].[_Fld432RRef]=[@P6] THEN [@P7] ELSE CASE WHEN [test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x01 AND [test_database].[dbo].[_Task22].[_Fld432RRef] as [T1].[_Fld432RRef]<>[@P8] THEN [@P9] ELSE CASE WHEN [test_database].[dbo].[_Reference129].[_IDRRef] as [T10].[_IDRRef] IS NOT NULL THEN CASE WHEN [Expr1042] THEN [@P17] ELSE [@P18] END ELSE [@P19] END END END))\n" +
            "0, 1, 6.84E+003, 0, 0.0286, 540, 14.7, 1,                  |--Nested Loops(Left Semi Join, PASSTHRU:([test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x01 AND [test_database].[dbo].[_Task22].[_Fld432RRef] as [T1].[_Fld432RRef]=[@P6] OR [test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x01 AND [test_database].[dbo].[_Task22].[_Fld432RRef] as [T1].[_Fld432RRef]<>[@P8] OR IsFalseOrNull [test_database].[dbo].[_Reference129].[_IDRRef] as [T10].[_IDRRef] IS NOT NULL), OUTER REFERENCES:([T1].[_IDRRef]), DEFINE:([Expr1042] = [PROBE VALUE]))\n" +
            "0, 1, 6.84E+003, 0, 0.0286, 539, 13.9, 1,                       |--Nested Loops(Left Outer Join, OUTER REFERENCES:([T11].[_Fld374RRef], [Expr1048]) WITH ORDERED PREFETCH)\n" +
            "0, 1, 6.83E+003, 0, 0.0286, 295, 11.4, 1,                       |    |--Nested Loops(Left Semi Join, OUTER REFERENCES:([T1].[_IDRRef], [T1].[_Fld315], [T2].[_Fld5103RRef], [Expr1009], [Expr1010], [T11].[_Fld372RRef]))\n" +
            "8206, 1, 6.83E+003, 0, 0.0285, 365, 11.3, 1,                       |    |    |--Nested Loops(Left Outer Join, OUTER REFERENCES:([T1].[_BusinessProcessRRef], [Expr1047]) WITH ORDERED PREFETCH)\n" +
            "8206, 1, 6.82E+003, 0.0113, 0.228, 333, 8.78, 1,                       |    |    |    |--Sort(ORDER BY:([T1].[_IDRRef] ASC))\n" +
            "8206, 1, 6.82E+003, 0, 0.07, 333, 8.54, 1,                       |    |    |    |    |--Hash Match(Right Outer Join, HASH:([T10].[_IDRRef])=([T9].[_Fld2134RRef]), RESIDUAL:([test_database].[dbo].[_Reference148].[_Fld2134RRef] as [T9].[_Fld2134RRef]=[test_database].[dbo].[_Reference129].[_IDRRef] as [T10].[_IDRRef]))\n" +
            "8, 1, 8, 0.00313, 0.000166, 23, 0.00329, 1,                       |    |    |    |         |--Index Seek(OBJECT:([test_database].[dbo].[_Reference129].[_Reference129_Descr] AS [T10]), SEEK:([T10].[_Fld315]=[@P31]) ORDERED FORWARD)\n" +
            "8206, 1, 6.82E+003, 0, 0.0224, 333, 8.47, 1,                       |    |    |    |         |--Merge Join(Right Outer Join, MERGE:([T9].[_IDRRef])=([T1].[_Fld428RRef]), RESIDUAL:([test_database].[dbo].[_Task22].[_Fld428RRef] as [T1].[_Fld428RRef]=[test_database].[dbo].[_Reference148].[_IDRRef] as [T9].[_IDRRef]))\n" +
            "834, 1, 843, 0.0216, 0.00108, 55, 0.0227, 1,                       |    |    |    |              |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Reference148].[_Reference148HPK] AS [T9]), SEEK:([T9].[_Fld315]=[@P30]) ORDERED FORWARD)\n" +
            "8206, 1, 6.82E+003, 0, 0.0286, 300, 8.43, 1,                       |    |    |    |              |--Nested Loops(Left Outer Join, OUTER REFERENCES:([T1].[_IDRRef]))\n" +
            "2532, 1, 474, 0.0113, 0.00668, 252, 3.24, 1,                       |    |    |    |                   |--Sort(ORDER BY:([T1].[_Fld428RRef] ASC))\n" +
            "2532, 1, 474, 0, 0.00686, 252, 3.22, 1,                       |    |    |    |                   |    |--Merge Join(Right Anti Semi Join, MERGE:([T29].[_Fld5117RRef], [T29].[_Fld5118RRef])=([T1].[_BusinessProcessRRef], [T1].[_IDRRef]), RESIDUAL:([test_database].[dbo].[_InfoRg5116].[_Fld5117RRef] as [T29].[_Fld5117RRef]=[test_database].[dbo].[_Task22].[_BusinessProcessRRef] as [T1].[_BusinessProcessRRef] AND [test_database].[dbo].[_InfoRg5116].[_Fld5118RRef] as [T29].[_Fld5118RRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]))\n" +
            "103, 1, 103, 0.00313, 0.00027, 39, 0.0034, 1,                       |    |    |    |                   |         |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5116].[_InfoRg5116_ByDims] AS [T29]), SEEK:([T29].[_Fld315]=[@P52]) ORDERED FORWARD)\n" +
            "2533, 1, 474, 0.0113, 0.00668, 252, 3.21, 1,                       |    |    |    |                   |         |--Sort(ORDER BY:([T1].[_BusinessProcessRRef] ASC, [T1].[_IDRRef] ASC))\n" +
            "2533, 1, 975, 0, 0.00407, 259, 3.19, 1,                       |    |    |    |                   |              |--Nested Loops(Inner Join, OUTER REFERENCES:([T1].[_IDRRef], [T1].[_Fld315], [Expr1046]) OPTIMIZED WITH UNORDERED PREFETCH)\n" +
            "6993, 1, 975, 0.00384, 0.00123, 45, 0.00507, 1,                       |    |    |    |                   |                   |--Index Seek(OBJECT:([test_database].[dbo].[_Task22].[_Task22_ByProcPointExec] AS [T1]), SEEK:([T1].[_Fld315]=[@P34] AND [T1].[_Executed]=0x00) ORDERED FORWARD)\n" +
            "2533, 6993, 474, 0.00313, 0.000158, 221, 3.18, 975,                       |    |    |    |                   |                   |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Task22].[_Task22HPK] AS [T1]), SEEK:([T1].[_Fld315]=[test_database].[dbo].[_Task22].[_Fld315] as [T1].[_Fld315] AND [T1].[_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]),  WHERE:([test_database].[dbo].[_Task22].[_Marked] as [T1].[_Marked]=0x00 AND [test_database].[dbo].[_Task22].[_Fld425] as [T1].[_Fld425]=0x00 AND ([test_database].[dbo].[_Task22].[_Fld427] as [T1].[_Fld427]<=[@P54] OR [test_database].[dbo].[_Task22].[_Fld427] as [T1].[_Fld427]=[@P55]) AND ([test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x01 AND [test_database].[dbo].[_Task22].[_Fld432RRef] as [T1].[_Fld432RRef]=[@P53] OR [test_database].[dbo].[_Task22].[_Fld431] as [T1].[_Fld431]=0x00)) LOOKUP ORDERED FORWARD)\n" +
            "8100, 2532, 14.4, 0, 6.01E-005, 55, 5.16, 474,                       |    |    |    |                   |--Nested Loops(Left Outer Join, WHERE:([test_database].[dbo].[_InfoRg4961].[_Fld4962RRef] as [T8].[_Fld4962RRef]=[test_database].[dbo].[_InfoRg5102].[_Fld5103RRef] as [T2].[_Fld5103RRef] AND [test_database].[dbo].[_InfoRg4961].[_Fld4963RRef] as [T8].[_Fld4963RRef]=[test_database].[dbo].[_Task22].[_Fld422RRef] as [T3].[_Fld422RRef] AND [test_database].[dbo].[_Reference129].[_IDRRef] as [T4].[_IDRRef] IS NOT NULL))\n" +
            "8100, 2532, 14.4, 0, 5.99E-005, 55, 4.43, 474,                       |    |    |    |                        |--Nested Loops(Left Outer Join, OUTER REFERENCES:([T2].[_Fld5104RRef]))\n" +
            "8100, 2532, 14.3, 0.00313, 0.000173, 51, 1.54, 474,                       |    |    |    |                        |    |--Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5102].[_InfoRg5102_ByDims5107] AS [T2]), SEEK:([T2].[_Fld315]=[@P29] AND [T2].[_Fld5104RRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]),  WHERE:([test_database].[dbo].[_InfoRg5102].[_Fld5105] as [T2].[_Fld5105]<=[@P26] AND ([test_database].[dbo].[_InfoRg5102].[_Fld5106] as [T2].[_Fld5106]>=[@P27] OR [test_database].[dbo].[_InfoRg5102].[_Fld5106] as [T2].[_Fld5106]=[@P28])) ORDERED FORWARD)\n" +
            "8100, 8100, 1, 0.00313, 0.000259, 39, 2.85, 6.8E+003,                       |    |    |    |                        |    |--Index Spool(SEEK:([T2].[_Fld5104RRef]=[test_database].[dbo].[_InfoRg5102].[_Fld5104RRef] as [T2].[_Fld5104RRef] AND [@P20]=[@P20] AND [@P21]=[@P21]))\n" +
            "2426, 2426, 1, 0, 4.18E-006, 39, 1.09, 474,                       |    |    |    |                        |         |--Nested Loops(Left Outer Join, OUTER REFERENCES:([T3].[_Fld422RRef]))\n" +
            "2426, 2426, 1, 0.00313, 0.000158, 23, 1.01, 474,                       |    |    |    |                        |              |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Task22].[_Task22HPK] AS [T3]), SEEK:([T3].[_Fld315]=[@P20] AND [T3].[_IDRRef]=[test_database].[dbo].[_InfoRg5102].[_Fld5104RRef] as [T2].[_Fld5104RRef]) ORDERED FORWARD)\n" +
            "2426, 2426, 1, 0.00313, 0.000158, 23, 0.0781, 474,                       |    |    |    |                        |              |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Reference129].[_Reference129HPK] AS [T4]), SEEK:([T4].[_Fld315]=[@P21] AND [T4].[_IDRRef]=[test_database].[dbo].[_Task22].[_Fld422RRef] as [T3].[_Fld422RRef]) ORDERED FORWARD)\n" +
            "0, 8100, 1, 0.01, 0.000101, 71, 0.699, 6.81E+003,                       |    |    |    |                        |--Table Spool\n" +
            "0, 0, 1, 0, 1E-007, 71, 0.00657, 1,                       |    |    |    |                             |--Compute Scalar(DEFINE:([Expr1009]=[test_database].[dbo].[_InfoRg4961].[_Fld4962RRef] as [T8].[_Fld4962RRef], [Expr1010]=[test_database].[dbo].[_InfoRg4961].[_Fld4963RRef] as [T8].[_Fld4963RRef]))\n" +
            "0, 1, 1, 0, 4.18E-006, 39, 0.00657, 1,                       |    |    |    |                                  |--Nested Loops(Inner Join, OUTER REFERENCES:([T7].[_Fld4962RRef], [T7].[_Fld4963RRef], [Expr1006]))\n" +
            "0, 1, 1, 0, 1.1E-006, 45, 0.00329, 1,                       |    |    |    |                                       |--Stream Aggregate(GROUP BY:([T7].[_Fld4962RRef], [T7].[_Fld4963RRef]) DEFINE:([Expr1006]=MAX([test_database].[dbo].[_InfoRg4961].[_Period] as [T7].[_Period])))\n" +
            "0, 1, 1, 0.00313, 0.000158, 66, 0.00328, 1,                       |    |    |    |                                       |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg4961].[_InfoRg4961_ByDims] AS [T7]), SEEK:([T7].[_Fld315]=[@P22]),  WHERE:([test_database].[dbo].[_InfoRg4961].[_Period] as [T7].[_Period]<=[@P23] AND [test_database].[dbo].[_InfoRg4961].[_Fld4964RRef] as [T7].[_Fld4964RRef]=[@P24] AND [test_database].[dbo].[_InfoRg4961].[_Active] as [T7].[_Active]=0x01) ORDERED FORWARD)\n" +
            "0, 0, 1, 0.00313, 0.000158, 40, 0.00328, 1,                       |    |    |    |                                       |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg4961].[_InfoRg4961_ByDims] AS [T8]), SEEK:([T8].[_Fld315]=[@P25] AND [T8].[_Fld4962RRef]=[test_database].[dbo].[_InfoRg4961].[_Fld4962RRef] as [T7].[_Fld4962RRef] AND [T8].[_Fld4963RRef]=[test_database].[dbo].[_InfoRg4961].[_Fld4963RRef] as [T7].[_Fld4963RRef] AND [T8].[_Fld4964RRef]=[@P24] AND [T8].[_Period]=[Expr1006]),  WHERE:([test_database].[dbo].[_InfoRg4961].[_Fld4965] as [T8].[_Fld4965]=0x01) ORDERED FORWARD)\n" +
            "8206, 8206, 1, 0.00313, 0.000158, 39, 2.53, 6.82E+003,                       |    |    |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_BPr19].[_BPr19HPK] AS [T11]), SEEK:([T11].[_Fld315]=[@P32] AND [T11].[_IDRRef]=[test_database].[dbo].[_Task22].[_BusinessProcessRRef] as [T1].[_BusinessProcessRRef]) ORDERED FORWARD)\n" +
            "0, 8206, 1, 0, 4E-007, 9, 0.0163, 6.83E+003,                       |    |    |--Concatenation\n" +
            "0, 8206, 1, 0, 1.28E-006, 9, 0.0156, 6.83E+003,                       |    |         |--Filter(WHERE:(STARTUP EXPR([test_database].[dbo].[_InfoRg5102].[_Fld5103RRef] as [T2].[_Fld5103RRef]=[@P36] OR [Expr1009] IS NOT NULL AND [Expr1010] IS NOT NULL)))\n" +
            "0, 0, 1, 0, 1.16E-006, 9, 0.00683, 6.83E+003,                       |    |         |    |--Constant Scan\n" +
            "0, 8206, 1, 0.00313, 0.000158, 9, 14.9, 6.83E+003,                       |    |         |--Index Seek(OBJECT:([test_database].[dbo].[_Task22_VT433].[_Task22_VT433_ByField436] AS [T15]), SEEK:([T15].[_Fld315]=[test_database].[dbo].[_Task22].[_Fld315] as [T1].[_Fld315] AND [T15].[_Fld435RRef]=[@P35] AND [T15].[_Task22_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]) ORDERED FORWARD)\n" +
            "0, 8206, 1, 0, 4.18E-006, 9, 14.9, 6.83E+003,                       |    |         |--Nested Loops(Left Semi Join)\n" +
            "162, 8206, 1, 0.00313, 0.000158, 9, 14.9, 6.83E+003,                       |    |         |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Task22_VT433].[_Task22_VT433_IntKeyInd] AS [T16]), SEEK:([T16].[_Fld315]=[test_database].[dbo].[_Task22].[_Fld315] as [T1].[_Fld315] AND [T16].[_Task22_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]) ORDERED FORWARD)\n" +
            "0, 162, 1, 0, 0.0001, 9, 14.9, 6.83E+003,                       |    |         |    |--Row Count Spool\n" +
            "0, 102, 1, 0, 4.18E-006, 9, 14.9, 6.83E+003,                       |    |         |         |--Nested Loops(Left Semi Join, OUTER REFERENCES:([T21].[_Fld5077RRef]) OPTIMIZED)\n" +
            "0, 102, 1, 0, 4.18E-006, 23, 2.22, 6.83E+003,                       |    |         |              |--Nested Loops(Inner Join, OUTER REFERENCES:([T20].[_Fld5077RRef], [Expr1021]))\n" +
            "0, 102, 1, 0, 1.1E-006, 29, 1.1, 6.83E+003,                       |    |         |              |    |--Stream Aggregate(GROUP BY:([T20].[_Fld5077RRef]) DEFINE:([Expr1021]=MAX([test_database].[dbo].[_InfoRg5075].[_Period] as [T20].[_Period])))\n" +
            "0, 102, 1, 0.00313, 0.000158, 50, 1.08, 6.83E+003,                       |    |         |              |    |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5075].[_InfoRg5075_ByDims] AS [T20]), SEEK:([T20].[_Fld315]=[@P37] AND [T20].[_Fld5076RRef]=[test_database].[dbo].[_BPr19].[_Fld372RRef] as [T11].[_Fld372RRef]),  WHERE:([test_database].[dbo].[_InfoRg5075].[_Period] as [T20].[_Period]<=[@P38] AND [test_database].[dbo].[_InfoRg5075].[_Fld5078RRef] as [T20].[_Fld5078RRef]=[@P39] AND [test_database].[dbo].[_InfoRg5075].[_Active] as [T20].[_Active]=0x01) ORDERED FORWARD)\n" +
            "0, 0, 1, 0.00313, 0.000158, 35, 1.08, 6.83E+003,                       |    |         |              |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5075].[_InfoRg5075_ByDims] AS [T21]), SEEK:([T21].[_Fld315]=[@P40] AND [T21].[_Fld5076RRef]=[test_database].[dbo].[_BPr19].[_Fld372RRef] as [T11].[_Fld372RRef] AND [T21].[_Fld5077RRef]=[test_database].[dbo].[_InfoRg5075].[_Fld5077RRef] as [T20].[_Fld5077RRef] AND [T21].[_Fld5078RRef]=[@P39] AND [T21].[_Period]=[Expr1021]),  WHERE:([test_database].[dbo].[_InfoRg5075].[_Period] as [T21].[_Period]<=[@P41] AND ([test_database].[dbo].[_InfoRg5075].[_Fld5079] as [T21].[_Fld5079]>=[@P42] OR [test_database].[dbo].[_InfoRg5075].[_Fld5079] as [T21].[_Fld5079]=[@P43])) ORDERED FORWARD)\n" +
            "0, 0, 1, 0.00313, 0.000158, 9, 14.9, 6.83E+003,                       |    |         |              |--Index Seek(OBJECT:([test_database].[dbo].[_Task22_VT433].[_Task22_VT433_ByField436] AS [T22]), SEEK:([T22].[_Fld315]=[test_database].[dbo].[_Task22].[_Fld315] as [T1].[_Fld315] AND [T22].[_Fld435RRef]=[test_database].[dbo].[_InfoRg5075].[_Fld5077RRef] as [T21].[_Fld5077RRef] AND [T22].[_Task22_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]) ORDERED FORWARD)\n" +
            "0, 8206, 1, 0, 4.18E-006, 9, 14.9, 6.83E+003,                       |    |         |--Nested Loops(Left Semi Join, WHERE:([test_database].[dbo].[_Task22_VT433].[_Fld435RRef] as [T23].[_Fld435RRef]=[test_database].[dbo].[_InfoRg5075].[_Fld5077RRef] as [T28].[_Fld5077RRef]))\n" +
            "162, 8206, 1, 0.00313, 0.000158, 23, 14.9, 6.83E+003,                       |    |              |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Task22_VT433].[_Task22_VT433_IntKeyInd] AS [T23]), SEEK:([T23].[_Fld315]=[test_database].[dbo].[_Task22].[_Fld315] as [T1].[_Fld315] AND [T23].[_Task22_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]) ORDERED FORWARD)\n" +
            "0, 162, 1, 0.01, 0.0001, 23, 0.701, 6.83E+003,                       |    |              |--Table Spool\n" +
            "0, 1, 1, 0, 4.18E-006, 23, 0.00657, 1,                       |    |                   |--Nested Loops(Inner Join, OUTER REFERENCES:([T27].[_Fld5077RRef], [Expr1031]))\n" +
            "0, 1, 1, 0, 1.1E-006, 29, 0.00329, 1,                       |    |                        |--Stream Aggregate(GROUP BY:([T27].[_Fld5077RRef]) DEFINE:([Expr1031]=MAX([test_database].[dbo].[_InfoRg5075].[_Period] as [T27].[_Period])))\n" +
            "0, 1, 1, 0.00313, 0.000158, 50, 0.00328, 1,                       |    |                        |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5075].[_InfoRg5075_ByDims] AS [T27]), SEEK:([T27].[_Fld315]=[@P44] AND [T27].[_Fld5076RRef]=0x00000000000000000000000000000000),  WHERE:([test_database].[dbo].[_InfoRg5075].[_Period] as [T27].[_Period]<=[@P45] AND [test_database].[dbo].[_InfoRg5075].[_Fld5078RRef] as [T27].[_Fld5078RRef]=[@P46] AND [test_database].[dbo].[_InfoRg5075].[_Active] as [T27].[_Active]=0x01) ORDERED FORWARD)\n" +
            "0, 0, 1, 0.00313, 0.000158, 35, 0.00328, 1,                       |    |                        |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5075].[_InfoRg5075_ByDims] AS [T28]), SEEK:([T28].[_Fld315]=[@P47] AND [T28].[_Fld5076RRef]=0x00000000000000000000000000000000 AND [T28].[_Fld5077RRef]=[test_database].[dbo].[_InfoRg5075].[_Fld5077RRef] as [T27].[_Fld5077RRef] AND [T28].[_Fld5078RRef]=[@P46] AND [T28].[_Period]=[Expr1031]),  WHERE:([test_database].[dbo].[_InfoRg5075].[_Period] as [T28].[_Period]<=[@P48] AND ([test_database].[dbo].[_InfoRg5075].[_Fld5079] as [T28].[_Fld5079]>=[@P49] OR [test_database].[dbo].[_InfoRg5075].[_Fld5079] as [T28].[_Fld5079]=[@P50])) ORDERED FORWARD)\n" +
            "0, 0, 1, 0.00313, 0.000158, 268, 2.49, 6.83E+003,                       |    |--Clustered Index Seek(OBJECT:([test_database].[dbo].[_Document172].[_Document172HPK] AS [T12]), SEEK:([T12].[_Fld315]=[@P33] AND [T12].[_IDRRef]=[test_database].[dbo].[_BPr19].[_Fld374RRef] as [T11].[_Fld374RRef]) ORDERED FORWARD)\n" +
            "0, 0, 1, 0, 0.0001, 9, 0.763, 6.84E+003,                       |--Row Count Spool\n" +
            "0, 0, 1, 0, 2E-007, 9, 0.0784, 474,                            |--Concatenation\n" +
            "0, 0, 1, 0, 4.8E-007, 9, 0.0784, 474,                                 |--Filter(WHERE:(STARTUP EXPR([@P10]=[@P11])))\n" +
            "0, 0, 1, 0, 1E-007, 9, 0.0782, 474,                                 |    |--Top(TOP EXPRESSION:((1)))\n" +
            "0, 0, 1, 0.00313, 0.000158, 9, 0.0781, 474,                                 |         |--Index Seek(OBJECT:([test_database].[dbo].[_Task22_VT433].[_Task22_VT433_ByField436] AS [T13]), SEEK:([T13].[_Fld315]=[@P12] AND [T13].[_Fld435RRef]=[@P13] AND [T13].[_Task22_IDRRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef]) ORDERED FORWARD)\n" +
            "0, 0, 1, 0, 4.8E-007, 9, 0.178, 474,                                 |--Filter(WHERE:(STARTUP EXPR([@P10]=[@P14])))\n" +
            "0, 0, 1, 0, 1E-007, 9, 0.178, 474,                                      |--Top(TOP EXPRESSION:((1)))\n" +
            "0, 0, 1, 0.00313, 0.000158, 9, 0.178, 474,                                           |--Index Seek(OBJECT:([test_database].[dbo].[_InfoRg5102].[_InfoRg5102_ByDims5107] AS [T14]), SEEK:([T14].[_Fld315]=[@P15] AND [T14].[_Fld5104RRef]=[test_database].[dbo].[_Task22].[_IDRRef] as [T1].[_IDRRef] AND [T14].[_Fld5103RRef]=[@P16]) ORDERED FORWARD)\n" +
            "',Context=Система.ПолучитьФорму : Задача.УП_Задачи.Форма.ФормаСписка\n";
}
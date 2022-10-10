package com.feedbeforeflight.enterprise1cfiles.reglog.reader;

import com.feedbeforeflight.enterprise1cfiles.reglog.data.LogFileItem;
import com.feedbeforeflight.enterprise1cfiles.reglog.data.LogFileItemFactory;
import com.feedbeforeflight.enterprise1cfiles.reglog.data.LogfileEventImportance;
import com.feedbeforeflight.enterprise1cfiles.reglog.data.TransactionState;
import com.feedbeforeflight.enterprise1cfiles.reglog.dictionary.*;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
public class LogFileFieldsMapper {

    private final SimpleDateFormat simpleDateFormat;
    private final Dictionary dictionary;
    private final ZoneId zoneId;

    private final String databaseName;
    private final String logfileName;
    private LogFileItemFactory itemFactory;

    public LogFileFieldsMapper(Dictionary dictionary, String timeZone, String databaseName, String logfileName, LogFileItemFactory itemFactory) {
        this.dictionary = dictionary;
        this.logfileName = logfileName;
        this.databaseName = databaseName;
        this.itemFactory = itemFactory;

        zoneId = ZoneId.of(timeZone);
        simpleDateFormat = new SimpleDateFormat("yyyyMMddkkmmss");
    }

    private String removeQuotes(String string) {
        if (string.equals("\"\"")) { return ""; }
        else { return string.substring(1, string.length() - 1); }
    }

    public LogFileItem mapFields(List<String> fieldSet, int rowNumber) {
        if (fieldSet == null) {
            return null;
        }

        if (fieldSet.size() < 17) {
            log.error(String.join(",", fieldSet));
            log.error("At row: " + String.valueOf(rowNumber));
            return null;
        }

        LogFileItem result = itemFactory.createLogFileItem();
        result.setFileName(logfileName);
        result.setRowNumber(rowNumber);
        result.setDatabaseName(databaseName);

        // 1) Дата и время в формате "yyyyMMddHHmmss", легко превращается в дату функцией Дата();
        try {
            result.setTimestamp(simpleDateFormat.parse(fieldSet.get(0)));
        } catch (ParseException e) {
            log.debug("mapFieldSet:parse timestamp", e);
            result.setTimestamp(null);
        }

        // 2) Статус транзакции – может принимать четыре значения "N" – "Отсутствует", "U" – "Зафиксирована", "R" – "Не завершена" и "C" – "Отменена";
        result.setTransactionState(TransactionState.getByLogID(fieldSet.get(1)));

        // 3) Транзакция в формате записи из двух элементов преобразованных в шестнадцатеричное число – первый – число секунд с 01.01.0001 00:00:00 умноженное на 10000, второй – номер транзакции;
        String transactionInfo = fieldSet.get(2);
        transactionInfo = transactionInfo.substring(1, transactionInfo.length() - 1);
        String[] transactionInfoItems = transactionInfo.split(",");
        long originalSeconds = Long.parseLong(transactionInfoItems[0], 16);
        if (originalSeconds != 0) {
            // original datetime in milliseconds is in local timezone,
            // and we convert it to Zulu time keeping in mind that offset sometimes depends on time
            Instant instant = Instant.ofEpochMilli(originalSeconds / 10 - LogfileUtils.unixTimeSeconds);
            int secondsOffset = zoneId.getRules().getOffset(instant).getTotalSeconds();
            Date date = new Date(originalSeconds / 10 - LogfileUtils.unixTimeSeconds - secondsOffset * 1000L);

            result.setTransactionDate(date);
            result.setTransactionNumber(Integer.parseInt(transactionInfoItems[1], 16));
        }

        // 4) Пользователь – указывается номер в массиве пользователей;
        DictionaryUser user = dictionary.getUser(Integer.parseInt(fieldSet.get(3)));
        result.setUsername(user == null ? "" : removeQuotes(user.Presentation()));
        result.setUserGUID(user == null ? "" : user.getGuid());

        // 5) Компьютер – указывается номер в массиве компьютеров;
        DictionaryComputer computer = dictionary.getComputer(Integer.parseInt(fieldSet.get(4)));
        result.setComputer(computer == null ? "" : removeQuotes(computer.Presentation()));

        // 6) Приложение – указывается номер в массиве приложений;
        DictionaryApplication application = dictionary.getApplication(Integer.parseInt(fieldSet.get(5)));
        result.setApplication(application == null ? "" : removeQuotes(application.Presentation()));

        // 7) Соединение – номер соединения;
        result.setConnection(Integer.parseInt(fieldSet.get(6)));

        // 8) Событие – указывается номер в массиве событий;
        DictionaryEvent event = dictionary.getEvent(Integer.parseInt(fieldSet.get(7)));
        result.setEvent(event == null ? "" : removeQuotes(event.Presentation()));

        // 9) Важность – может принимать четыре значения – "I" – "Информация", "E" – "Ошибки",
        // "W" – "Предупреждения" и "N" – "Примечания";
        result.setEventImportance(LogfileEventImportance.getByLogID(fieldSet.get(8)));

        // 10) Комментарий – любой текст в кавычках;
        result.setComment(removeQuotes(fieldSet.get(9)));

        // 11) Метаданные – указывается номер в массиве метаданных;
        DictionaryMetadata dictionaryMetadata= dictionary.getMetadata(Integer.parseInt(fieldSet.get(10)));
        result.setMetadata(dictionaryMetadata == null ? "" : removeQuotes(dictionaryMetadata.Presentation()));
        result.setMetadataGUID(dictionaryMetadata == null ? "" : dictionaryMetadata.getGuid());

        // 12) Данные – самый хитрый элемент, содержащий вложенную запись;
        result.setData(fieldSet.get(11));

        // 13) Представление данных – текст в кавычках;
        result.setDataRepresentation(removeQuotes(fieldSet.get(12)));

        // 14) Сервер – указывается номер в массиве серверов;
        DictionaryServer server = dictionary.getServer(Integer.parseInt(fieldSet.get(13)));
        result.setServer(server == null ? "" : removeQuotes(server.Presentation()));

        // 15) Основной порт – указывается номер в массиве основных портов;
        DictionaryMainPort mainPort = dictionary.getMainPort(Integer.parseInt(fieldSet.get(14)));
        result.setMainPort(mainPort == null ? 0 : mainPort.Presentation());

        // 16) Вспомогательный порт – указывается номер в массиве вспомогательных портов;
        int auxiliaryPortNumber = Integer.parseInt(fieldSet.get(15));
        if (auxiliaryPortNumber == 0) {
            result.setAuxiliaryPort(0);
        }
        else {
            DictionaryAuxiliaryPort auxiliaryPort = dictionary.getAuxiliaryPort(auxiliaryPortNumber);
            result.setAuxiliaryPort(auxiliaryPort == null ? 0 : auxiliaryPort.Presentation());
        }

        // 17) Сеанс – номер сеанса;
        result.setSession(Integer.parseInt(fieldSet.get(16)));

        return result;
    }

}

// https://infostart.ru/1c/articles/182061/
//        1) Дата и время в формате "yyyyMMddHHmmss", легко превращается в дату функцией Дата();
//        2) Статус транзакции – может принимать четыре значения "N" – "Отсутствует", "U" – "Зафиксирована", "R" – "Не завершена" и "C" – "Отменена";
//        3) Транзакция в формате записи из двух элементов преобразованных в шестнадцатеричное число – первый – число секунд с 01.01.0001 00:00:00 умноженное на 10000, второй – номер транзакции;
//        4) Пользователь – указывается номер в массиве пользователей;
//        5) Компьютер – указывается номер в массиве компьютеров;
//        6) Приложение – указывается номер в массиве приложений;
//        7) Соединение – номер соединения;
//        8) Событие – указывается номер в массиве событий;
//        9) Важность – может принимать четыре значения – "I" – "Информация", "E" – "Ошибки",
//        "W" – "Предупреждения" и "N" – "Примечания";
//        10) Комментарий – любой текст в кавычках;
//        11) Метаданные – указывается номер в массиве метаданных;
//        12) Данные – самый хитрый элемент, содержащий вложенную запись;
//        13) Представление данных – текст в кавычках;
//        14) Сервер – указывается номер в массиве серверов;
//        15) Основной порт – указывается номер в массиве основных портов;
//        16) Вспомогательный порт – указывается номер в массиве вспомогательных портов;
//        17) Сеанс – номер сеанса;
//        18) Количество дополнительных метаданных, номера которых будут перечислены в следующих элементах записи. Именно 18-й элемент определяет длину записи, т.к. дальше будут следовать столько элементов сколько указано здесь + один последний, назначение которого пока не определено и обычно там "{0}". Возможно это просто маркер окончания записи. Так же есть идея что {0} похоже на пустой массив.

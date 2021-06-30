package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * Преобразование даты, времени полученных с сайта https://www.sql.ru/forum/job-offers
 */
public class SqlRuDateTimeParser implements DateTimeParser {
    /**
     * Константа для преобразования наименования месяцев.
     */
    private static final Map<String, String> MAP_MONTH = Map.ofEntries(
            Map.entry("янв", "январь"),
            Map.entry("фев", "февраль"),
            Map.entry("мар", "март"),
            Map.entry("апр", "апрель"),
            Map.entry("май", "май"),
            Map.entry("июн", "июнь"),
            Map.entry("июл", "июль"),
            Map.entry("авг", "август"),
            Map.entry("сен", "сентябрь"),
            Map.entry("окт", "октябрь"),
            Map.entry("ноя", "ноябрь"),
            Map.entry("дек", "декабрь")
    );

    @Override
    public LocalDateTime parse(String parse) {
        String[] array = parse.split(",");
        array[0] = ruMonthCorrector(array[0]);
        array[1] = array[1].trim();
        LocalDate localDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d LLLL yy", new Locale("ru", "RU"));
        if (array[0].equals("сегодня")) {
            localDate = LocalDate.now();
        } else if (array[0].equals("вчера")) {
            localDate = LocalDate.now().minusDays(1);
        } else {
            localDate = LocalDate.parse(array[0], formatter);
        }
        LocalTime localTime = LocalTime.parse(array[1], DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(localDate, localTime);
    }

    private static String ruMonthCorrector(String str) {
        String[] array = str.split(" ");
        String result = str;
        if (array.length == 3) {
            array[1] = MAP_MONTH.get(array[1]);
            String delimiter = " ";
            result = String.join(delimiter, array);
        }
        return result;
    }
}

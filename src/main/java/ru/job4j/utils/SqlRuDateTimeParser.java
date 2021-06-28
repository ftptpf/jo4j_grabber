package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Преобразование даты, времени полученных с сайта https://www.sql.ru/forum/job-offers
 */
public class SqlRuDateTimeParser implements DateTimeParser {

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
            switch (array[1]) {
                case "янв":
                    array[1] = "январь";
                    break;
                case "фев":
                    array[1] = "февраль";
                    break;
                case "мар":
                    array[1] = "март";
                    break;
                case "апр":
                    array[1] = "апрель";
                    break;
                case "май":
                    array[1] = "май";
                    break;
                case "июн":
                    array[1] = "июнь";
                    break;
                case "июл":
                    array[1] = "июль";
                    break;
                case "авг":
                    array[1] = "август";
                    break;
                case "сен":
                    array[1] = "сентябрь";
                    break;
                case "окт":
                    array[1] = "октябрь";
                    break;
                case "ноя":
                    array[1] = "ньябрь";
                    break;
                case "дек":
                    array[1] = "декабрь";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + str);
            }
            String delimiter = " ";
            result = String.join(delimiter, array);
        }
        return result;
    }
}

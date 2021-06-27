package ru.job4j.utils;

import java.time.LocalDateTime;

/**
 * Интерфейс преобразования даты и времени.
 */
public interface DateTimeParser {
    LocalDateTime parse(String parse);
}

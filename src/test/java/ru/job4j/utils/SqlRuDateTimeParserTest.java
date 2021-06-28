package ru.job4j.utils;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class SqlRuDateTimeParserTest {

/*    @Test
    public void parseToday() {
        String today = "сегодня, 10:59";
        SqlRuDateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        LocalDateTime ldt = sqlRuDateTimeParser.parse(today);
        assertThat("2021-06-28T10:59", is(ldt.toString()));
    }

    @Test
    public void parseYesterday() {
        String today = "вчера, 19:31";
        SqlRuDateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        LocalDateTime ldt = sqlRuDateTimeParser.parse(today);
        assertThat("2021-06-27T19:31", is(ldt.toString()));
    }*/

    @Test
    public void parseOther() {
        String today = "21 июн 21, 01:10";
        SqlRuDateTimeParser sqlRuDateTimeParser = new SqlRuDateTimeParser();
        LocalDateTime ldt = sqlRuDateTimeParser.parse(today);
        assertThat("2021-06-21T01:10", is(ldt.toString()));
    }
}

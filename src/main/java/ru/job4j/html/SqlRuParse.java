package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

/**
 * Получение информации о вакансиях с первых пяти страниц сайта https://www.sql.ru/forum/job-offers
 */
public class SqlRuParse {
    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            String url = "https://www.sql.ru/forum/job-offers/" + i;
            Document doc = Jsoup.connect(url).get();
            Elements row = doc.select(".postslisttopic");
            Elements dataRows = doc.select(".altCol");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element data = td.parent().child(5);
                System.out.println(data.text());
                System.out.print(new SqlRuDateTimeParser().parse(data.text()));
                System.out.println();
            }
        }
    }
}

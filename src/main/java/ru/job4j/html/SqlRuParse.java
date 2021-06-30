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
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(loadPost(href.attr("href")));
                Element data = td.parent().child(5);
                System.out.println(data.text());
                System.out.println(new SqlRuDateTimeParser().parse(data.text()));
            }
        }
    }

    /**
     * Получаем подробное описание вакансии.
     * @param url ссылка на вакансию
     * @return текст вакансии
     * @throws IOException
     */
    private static String loadPost(String url) throws IOException {
        Document docPost = Jsoup.connect(url).get();
        Element table = docPost.select("table[class=msgTable]").first();
        Element post = table.select("td[class=msgBody]").get(1);
        return post.text();
    }
}

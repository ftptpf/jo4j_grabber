package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.utils.SqlRuDateTimeParser;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсинг вакансий с сайта https://www.sql.ru/forum/job-offers
 */
public class SqlRuParse implements Parse {
    public static void main(String[] args) throws IOException {
        String link = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse parse = new SqlRuParse();
        parse.list(link);
    }

    /**
     * Загружаем список вакансий первых пяти страниц
     * @param link
     * @return
     * @throws IOException
     */
    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> resultPostList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String url = link + i;
            Document doc = Jsoup.connect(url).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                String postLink = href.attr("href");
                resultPostList.add(detail(postLink));
            }
        }
        System.out.println(resultPostList); // вывод на консоль для дополнительной проверки
        return resultPostList;
    }

    /**
     * Загружаем детали вакансии.
     * @param link
     * @return
     * @throws IOException
     */
    @Override
    public Post detail(String link) throws IOException {
        Document docPost = Jsoup.connect(link).get();
        Element table = docPost.select("table[class=msgTable]").first();
        Element title = table.select("td[class=messageHeader]").first();
        String titlePost = title.text();
        Element post = table.select("td[class=msgBody]").get(1);
        String description = post.text();
        Element data = table.select("td[class=msgFooter]").first();
        String[] array = data.text().split("\\[");
        String dataAndTime = array[0];
        LocalDateTime created = new SqlRuDateTimeParser().parse(dataAndTime);
        return new Post(titlePost, link, description, created);
    }
}

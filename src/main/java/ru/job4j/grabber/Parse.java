package ru.job4j.grabber;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс извлечения данных с сайта.
 */
public interface Parse {
    List<Post> list(String link) throws IOException;
    Post detail(String link) throws IOException;
}

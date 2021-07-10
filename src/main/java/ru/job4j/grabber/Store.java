package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс взаимодействия с базой данных.
 */
public interface Store {
    void save(Post post);
    List<Post> getAll();
    Post findById(int id);
    Set<String> getLinksFromBase();
    LocalDateTime getMaxDateTimeFromBase();
}

package ru.job4j.grabber;

import java.util.List;

/**
 * Интерфейс взаимодействия с базой данных.
 */
public interface Store {
    void save(Post post);
    List<Post> getAll();
    Post findById(int id);
}

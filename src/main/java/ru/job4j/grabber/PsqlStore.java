package ru.job4j.grabber;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Подключаемся к базе данных.
 * Сохраняем информацию о вакансиях.
 * Выполняем выгрузку всех вакансий из базы.
 * Выполняем поиск в базе вакансий по ID.
 */
public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("db.url"),
                    cfg.getProperty("db.login"),
                    cfg.getProperty("db.password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get("resources/grabber.properties");
        Properties properties = new Properties();
        try (BufferedInputStream bf = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            properties.load(bf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(properties);
        psqlStore.save(new Post("Вакансия 1",
                "mail.ru/1",
                "Вакансия администратора в СПБ",
                LocalDateTime.of(2020, 5, 6, 12, 20)));
        psqlStore.save(new Post("Вакансия 2",
                "mail.ru/2",
                "Вакансия SEO специалиста Томск",
                LocalDateTime.of(2021, 6, 9, 1, 59)));
        psqlStore.save(new Post("Вакансия 3 (с неуникальным URL)",
                "mail.ru/2",
                "Вакансия 3D дизайнера Екатеринбург",
                LocalDateTime.of(2021, 7, 16, 14, 1)));
        System.out.println(psqlStore.getAll());
        System.out.println(psqlStore.findById(34));
        System.out.println(psqlStore.findById(35));
        System.out.println(psqlStore.findById(36));
        try {
            psqlStore.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Добавляем информацию о вакансии в базу данных.
     * @param post
     */
    @Override
    public void save(Post post) {
        try (PreparedStatement prStatement = cnn.prepareStatement(
                "INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?)")) {
            prStatement.setString(1, post.getTitle());
            prStatement.setString(2, post.getDescription());
            prStatement.setString(3, post.getLink());
            prStatement.setTimestamp(4, new Timestamp(post.getCreated().getNano()));
            prStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получаем все вакансии из базы данных.
     * @return
     */
    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (PreparedStatement preparedStatement = cnn.prepareStatement("SELECT * FROM post")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post();
                    post.setId(resultSet.getInt("id"));
                    post.setTitle(resultSet.getString("name"));
                    post.setDescription(resultSet.getString("text"));
                    post.setLink(resultSet.getString("link"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    result.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Ищем вакансию по ID.
     * @param id
     * @return вакансия
     */
    @Override
    public Post findById(int id) {
        Post post = new Post();
        try (PreparedStatement prStatement = cnn.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            prStatement.setInt(1, id);
            try (ResultSet resultSet = prStatement.executeQuery()) {
                while (resultSet.next()) {
                    post.setId(id);
                    post.setTitle(resultSet.getString("name"));
                    post.setLink(resultSet.getString("link"));
                    post.setDescription(resultSet.getString("text"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}

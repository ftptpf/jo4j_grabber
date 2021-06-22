package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Вывод сообщения на консоль с заданным временным интервалом.
 */
public class AlertRabbit {
    private static Connection connection;
    private static Path path = Paths.get("resources/rabbit.properties"); // путь к properties файлу
    private static Properties properties = new Properties(); // настройки (properties)

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        initConnection();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); // создаем планировщик
            scheduler.start(); // запускаем планировщик
            JobDetail job = newJob(Rabbit.class) // определяем выполняемую работу
                    .build();
            SimpleScheduleBuilder times = simpleSchedule() // создаем временной интервал для тригера
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger() // определяем условия по которым будет выполняться работа
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger); // сообщаем планировщику, что работа будет выполняться по заданному тригеру
            Thread.sleep(10000); // приостанавливаем выполнение потока на 10 секунд
            scheduler.shutdown(); // останавливаем работу планировщика
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    /**
     * Вывод текста на консоль, запись в базу текущей даты и времени выполнения job.
     */
    public static class Rabbit implements Job {
        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            insertToTable(new Timestamp(System.currentTimeMillis()));
        }
    }

    /**
     * Загрузка настроек из properties файла.
     */
    private static void loadProperties() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path.toFile()))) {
            properties.load(bufferedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание соединения с базой данных.
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static void initConnection() throws ClassNotFoundException, SQLException {
        loadProperties();
        Class.forName("org.postgresql.Driver");
        String url = properties.getProperty("db.url");
        String login = properties.getProperty("db.login");
        String password = properties.getProperty("db.password");
        connection = DriverManager.getConnection(url, login, password);
        createTable();
    }

    /**
     * Создание таблицы rabbit в базе данных PostgreSQL.
     * @throws SQLException
     */
    private static void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS rabbit (%s,%s);",
                            "id serial primary key",
                            "created_date timestamp"
            );
            statement.execute(sql);
        }
    }

    /**
     * Вставляем данные в таблицу.
     * @param timestamp время
     */
    private static void insertToTable(Timestamp timestamp) {
        try (PreparedStatement prStatement = connection.prepareStatement("INSERT INTO rabbit (created_date) VALUES (?)")) {
            prStatement.setTimestamp(1, timestamp);
            prStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

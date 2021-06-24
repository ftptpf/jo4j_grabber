package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Вывод сообщения на консоль с заданным временным интервалом.
 * Сохраняем в базу данные - дату и время выполнения job.
 */
public class AlertRabbit {
    private static Path path = Paths.get("resources/rabbit.properties"); // путь к properties файлу
    private static Properties properties = new Properties(); // настройки (properties)

    public static void main(String[] args) throws ClassNotFoundException {
        loadProperties();
        Class.forName("org.postgresql.Driver");
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.login"),
                properties.getProperty("db.password"))) {
            createTable(connection);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); // создаем планировщик
            scheduler.start(); // запускаем планировщик
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("connection", connection);
            JobDetail job = newJob(Rabbit.class) // определяем выполняемую работу
                    .usingJobData(jobDataMap)
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
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection con = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            insertToTable(new Timestamp(System.currentTimeMillis()), con);
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
     * Создание таблицы rabbit в базе данных PostgreSQL.
     *
     * @throws SQLException
     */
    private static void createTable(Connection con) throws SQLException {
        try (Statement statement = con.createStatement()) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS rabbit (%s,%s);",
                    "id serial primary key",
                    "created_date timestamp"
            );
            statement.execute(sql);
        }
    }

    /**
     * Вставляем данные в таблицу.
     *
     * @param timestamp время
     */
    private static void insertToTable(Timestamp timestamp, Connection con) {
        try (PreparedStatement prStatement = con.prepareStatement("INSERT INTO rabbit (created_date) VALUES (?)")) {
            prStatement.setTimestamp(1, timestamp);
            prStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

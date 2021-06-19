package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

/**
 * Вывод сообщения на консоль с заданным временным интервалом.
 */
public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler(); // создаем планировщик
            scheduler.start(); // запускаем планировщик
            JobDetail job = newJob(Rabbit.class).build(); // определяем выполняемую работу
            SimpleScheduleBuilder times = simpleSchedule() // создаем временной интервал для тригера
                    .withIntervalInSeconds(getTimePeriod())
                    .repeatForever();
            Trigger trigger = newTrigger() // определяем условия по которым будет выполняться работа
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger); // сообщаем планировщику, что работа будет выполняться по заданному тригеру
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    /**
     * Вывод текста на консоль.
     */
    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }

    /**
     * Загружаем из properties файла интервал времени с которым будет запускаться расписание.
     * @return интервал времени в секундах.
     */
    private static Integer getTimePeriod() {
        Properties pr = new Properties();
        try (BufferedInputStream bfStream = new BufferedInputStream(
                new FileInputStream("resources/rabbit.properties"))) {
            pr.load(bfStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(pr.getProperty("rabbit.interval"));
    }
}

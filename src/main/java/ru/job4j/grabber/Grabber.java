package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    /**
     * Сохраняем и извлекаем данные о вакансиях из базы данных PostgreSQL.
     * @return
     */
    public Store store() {
        return new PsqlStore(cfg);
    }

    /**
     * Создаем и запускаем планировщик.
     * @return
     * @throws SchedulerException
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Загружаем настройки.
     * @throws IOException
     */
    public void cfg() throws IOException {
        try (InputStream in = new FileInputStream("resources/grabber.properties")) {
            cfg.load(in);
        }
    }

    /**
     * Запускаем работу по расписанию.
     * @param parse
     * @param store
     * @param scheduler
     * @throws SchedulerException
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class) // определяем выполняемую работу
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule() // создаем временной интервал для тригера
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger() // определяем условия по которым будет выполняться работа
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        private Set<String> setPostLinksFromBase = new HashSet<>();
        private  LocalDateTime maxDateTime;



        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            String link = "https://www.sql.ru/forum/job-offers/";
            setPostLinksFromBase = store.getLinksFromBase();
            maxDateTime = store.getMaxDateTimeFromBase();
            List<Post> listPost = new ArrayList<>();
            try {
                listPost = parse.list(link);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (listPost.size() != 0) {
                for (Post post : listPost) {
                    if ((setPostLinksFromBase.isEmpty())
                            || (maxDateTime.isBefore(post.getCreated())
                            && !setPostLinksFromBase.contains(post.getLink()))) {
                        store.save(post);
                        System.out.println("В базу добавлена новая вакансия " + post.getTitle()
                                + "время создания её на сайте" + post.getCreated());
                    }
                }
                /* TODO impl logic */
            }
            System.out.println(LocalDateTime.now()
                    + " Выполнена проверка на наличие новых вакансий на сайте.");
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        Parse parse = new SqlRuParse(new SqlRuDateTimeParser());
        grab.init(parse, store, scheduler);
    }
}

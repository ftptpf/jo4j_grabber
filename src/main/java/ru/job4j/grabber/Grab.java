package ru.job4j.grabber;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Интерфейс периодического запуска сбора данных с сайта.
 */
public interface Grab {
    void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException;
}

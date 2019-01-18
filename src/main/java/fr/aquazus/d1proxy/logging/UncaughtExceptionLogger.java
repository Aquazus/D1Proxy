package fr.aquazus.d1proxy.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        log.error("An error occurred", ex);
    }
}

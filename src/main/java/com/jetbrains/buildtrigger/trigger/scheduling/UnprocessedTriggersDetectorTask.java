package com.jetbrains.buildtrigger.trigger.scheduling;

import com.jetbrains.buildtrigger.trigger.service.BuildTriggerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Периодическая задача, позволяющая выявлять и обрабатывать необработанные триггеры.
 * Ответственна только за исполнение по расписанию, непосредственно поиск и обработка делегируются.
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
@Service
public class UnprocessedTriggersDetectorTask {

    private static final Logger log = LoggerFactory.getLogger(UnprocessedTriggersDetectorTask.class);

    private final ExecutorService executor;
    private final BuildTriggerManager buildTriggerManager;

    public UnprocessedTriggersDetectorTask(ThreadPoolExecutor triggerProcessorThreadPoolExecutor,
                                           BuildTriggerManager buildTriggerManager) {
        this.executor = triggerProcessorThreadPoolExecutor;
        this.buildTriggerManager = buildTriggerManager;
    }

    @Scheduled(fixedDelayString = "${scheduling.unprocessed-triggers.fixed-delay-ms}")
    public void execute() {
        try {
            executor.execute(buildTriggerManager::detectUnprocessedTrigger);
        } catch (RejectedExecutionException e) {
            log.warn("There are no available executors to execute task", e);
        }
    }
}

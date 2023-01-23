package com.jetbrains.buildtrigger.trigger.service;

import com.jetbrains.buildtrigger.config.properties.TriggerProperties;
import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.dao.TriggerRepository;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.domain.TriggerType;
import com.jetbrains.buildtrigger.trigger.service.processing.TriggerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для управления триггерами сборок
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Service
public class BuildTriggerManager {

    private static final Logger log = LoggerFactory.getLogger(BuildTriggerManager.class);

    private final TriggerRepository triggerRepository;
    private final Map<TriggerType, TriggerProcessor> triggerProcessors;
    private final TriggerProperties triggerProperties;

    @Autowired
    public BuildTriggerManager(TriggerRepository triggerRepository,
                               Map<TriggerType, TriggerProcessor> triggerProcessors,
                               TriggerProperties triggerProperties) {
        this.triggerRepository = triggerRepository;
        this.triggerProcessors = triggerProcessors;
        this.triggerProperties = triggerProperties;
    }

    /**
     * Определить необработанный триггер.
     * Необработанным считается триггер, у которого {@link BuildTrigger#getNextExecutionTime()} меньше или равно текущему времени.
     *
     * Чтобы обработка была эксклюзивной относительно других нод и потоков, достаём триггер с пессимистической блокировкой по строке.
     *
     * Если триггер был найден, в рамках транзакции, пока действует блокировка, делегируем обработку соответствующему обработчику,
     * на основании {@link BuildTrigger#getType()}.
     *
     * Если результат обработки успешный, время следующего исполнения обновляется согласно выбранным настройкам триггера.
     * Иначе обработка будет отложена на константный период времени, согласно {@link TriggerProperties#getNextExecutionDelayOnError()}.
     */
    @Transactional
    public Result<Void, Void> detectAndProcess() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        Optional<BuildTrigger> unprocessedOpt = triggerRepository.fetchUnprocessedWithLock(now);
        if (unprocessedOpt.isEmpty()) {
            return Result.successEmpty();
        }

        BuildTrigger trigger = unprocessedOpt.orElseThrow();
        log.info("Unprocessed trigger detected: trigger={}", trigger);

        TriggerProcessor processor = Optional.ofNullable(triggerProcessors.get(trigger.getType())).orElseThrow();
        Result<Void, Void> processingResult = processor.process(trigger);
        if (processingResult.isError()) {
            ZonedDateTime nextExecution = now.plus(triggerProperties.getNextExecutionDelayOnError());
            updateNextExecutionTime(nextExecution, now, trigger);
            log.warn("Error while processing trigger. Next execution will be delayed: triggerId={}, nextExecution={}",
                    trigger.getId(), nextExecution);
            return Result.errorEmpty();
        }

        updateNextExecutionTime(processor.getNextExecutionTime(now, trigger).orElse(null), now, trigger);
        log.info("Trigger has successfully been processed: nextExecutionTime={}", trigger.getNextExecutionTime().orElse(null));
        return Result.successEmpty();
    }

    private void updateNextExecutionTime(@Nullable ZonedDateTime nextExecutionTime,
                                         @Nonnull ZonedDateTime now,
                                         @Nonnull BuildTrigger trigger) {
        trigger.setUpdated(now);
        trigger.setNextExecutionTime(nextExecutionTime);

        triggerRepository.save(trigger);
    }

    /**
     * Найти триггер сборок по идентификатору
     *
     * @param triggerId уникальный идентификатор триггера
     */
    @Nonnull
    @Transactional
    public Optional<BuildTrigger> findTriggerById(@Nonnull Long triggerId) {
        Optional<BuildTrigger> found = triggerRepository.findById(triggerId);
        log.info("findById(): found={}", found.orElse(null));
        return found;
    }

    /**
     * Создать новый триггер сборок
     *
     * @param trigger данные триггера
     */
    @Transactional
    @Nonnull
    public BuildTrigger createTrigger(@Nonnull BuildTrigger trigger) {
        BuildTrigger saved = triggerRepository.save(trigger);
        log.info("createTrigger(): savedTrigger={}", saved);
        return saved;
    }

    /**
     * Обновить триггер сборок
     *
     * @param trigger обновлённые данные триггера
     * @return результат выполнения операции
     */
    @Transactional
    @Nonnull
    public Result<Void, Void> updateTrigger(@Nonnull BuildTrigger trigger) {
        log.info("updateTrigger(): updatedTrigger={}", trigger);

        var locked = triggerRepository.fetchForUpdateById(trigger.getId());
        if (locked.isEmpty()) {
            log.info("Couldn't update: trigger is locked.");
            return Result.errorEmpty();
        }
        triggerRepository.save(trigger);

        return Result.successEmpty();
    }

    /**
     * Удалить триггер сборок по идентификатору
     *
     * @param triggerId уникальный идентификатор триггера
     * @return результат выполнения операции
     */
    @Transactional
    public Result<Void, Void> deleteTrigger(@Nonnull Long triggerId) {
        log.info("deleteTrigger(): triggerId={}", triggerId);
        Optional<BuildTrigger> trigger = triggerRepository.fetchForUpdateById(triggerId);
        if (trigger.isEmpty()) {
            log.info("Couldn't delete: trigger is locked.");
            return Result.errorEmpty();
        }

        triggerRepository.deleteById(triggerId);
        log.info("Trigger has successfully been deleted");

        return Result.successEmpty();
    }

    /**
     * Существует ли триггер с переданным идентификатором
     *
     * @param triggerId идентификатор триггера
     * @return true - существует, false - нет
     */
    public boolean existsById(@Nonnull Long triggerId) {
        return triggerRepository.existsById(triggerId);
    }
}

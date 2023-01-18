package com.jetbrains.buildtrigger.trigger.dao;

import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * DAO для работы с {@link BuildTrigger}
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Repository
public interface TriggerRepository extends JpaRepository<BuildTrigger, Long> {

    /**
     * Режим Spring Data, при котором если строка заблокирована, не будем ждать её разблокировки.
     */
    String NO_WAIT_MODE = "-2";

    /**
     * Достать необработанный триггер (у которого next_execution_time меньше или равно текущему времени).
     * На запись берётся пессимистическая row-level блокировка.
     * Уже заблокированные строки не попадают в выборку.
     * Достаётся триггер с самой ранней датой next_execution_time.
     *
     * @param now текущее время
     * @return необработанный триггер с блокировкой, либо пустой {@link Optional}, если обрабатывать нечего.
     */
    @Query(value = "SELECT * FROM build_trigger " +
            "WHERE next_execution_time IS NOT NULL " +
            "AND next_execution_time <= :now " +
            "ORDER BY next_execution_time " +
            "LIMIT 1 " +
            "FOR UPDATE " +
            "SKIP LOCKED",
            nativeQuery = true)
    Optional<BuildTrigger> fetchUnprocessedWithLock(@Nonnull @Param("now") ZonedDateTime now);


    /**
     * Достать триггер по идентификатору для обновления - с пессимистической row-level блокировкой.
     *
     * @param id идентификатор блокировки
     * @return триггер с блокировкой, либо пустой {@link Optional}
     */
    @Query("SELECT trigger FROM BuildTrigger trigger WHERE trigger.id = :id ORDER BY trigger.id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = AvailableSettings.JPA_LOCK_TIMEOUT, value = NO_WAIT_MODE))
    Optional<BuildTrigger> fetchForUpdateById(@Param("id") Long id);
}

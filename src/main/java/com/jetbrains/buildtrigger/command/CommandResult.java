package com.jetbrains.buildtrigger.command;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Структура результата обработки запроса в команде
 *
 * @param <SuccessT> тип класса результата успеха
 * @param <ErrorT>> тип класса результата ошибки бизнес логики
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class CommandResult<SuccessT, ErrorT> {

    /**
     * Статус обработки команды
     */
    @Nonnull
    private final Status status;

    /**
     * Данные результата успешной обработки команды.
     * Могут присутствовать в случае успеха.
     */
    @Nullable
    private final SuccessT success;

    /**
     * Данные результата неуспешной обработки команды.
     * Присутствуют в случае ошибки.
     */
    @Nullable
    private final ErrorT error;

    private CommandResult(@Nonnull Status status,
                          @Nullable SuccessT success,
                          @Nullable ErrorT error) {
        this.status = Objects.requireNonNull(status, "status");
        if (error == null && status == Status.ERROR) {
            throw new IllegalArgumentException("Error result should be set when status is ERROR");
        }
        this.success = success;
        this.error = error;
    }

    /**
     * Построить успешный непустой результат обработки команды
     * @param success данные ответа
     */
    public static <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> success(SuccessT success) {
        return new CommandResult<>(Status.SUCCESS, success, null);
    }

    /**
     * Построить успешный пустой результат обработки команды, когда данные ответа не требуются
     */
    public static <SuccessT, ErrorT> CommandResult<Void, ErrorT> successEmpty() {
        return new CommandResult<>(Status.SUCCESS, null, null);
    }

    /**
     * Построить неуспешный результат обработки команды, когда обработка завершилась с ошибкой
     *
     * @param error данные ошибки
     */
    public static <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> error(ErrorT error) {
        return new CommandResult<>(Status.ERROR, null, error);
    }

    /**
     * Возвращает статус обработки запроса.
     *
     * @return статус обработки запроса
     */
    @Nonnull
    public Status getStatus() {
        return status;
    }

    public boolean isFail() {
        return status == Status.ERROR;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    @Nonnull
    public Optional<SuccessT> getSuccess() {
        return Optional.ofNullable(success);
    }

    @Nonnull
    public SuccessT getSuccessOrThrow() {
        return Objects.requireNonNull(success, "success");
    }

    @Nonnull
    public Optional<ErrorT> getError() {
        return Optional.ofNullable(error);
    }

    @Nonnull
    public ErrorT getErrorOrThrow() {
        return Objects.requireNonNull(error, "error");
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("success", success)
                .add("error", error)
                .toString();
    }

    /**
     * Статус обработки запроса.
     */
    public enum Status {

        /**
         * Запрос обработан успешно, результат обработки находится в {@link CommandResult#getSuccess()}.
         */
        SUCCESS("success"),

        /**
         * Запрос обработан неуспешно.
         */
        ERROR("error");

        private final String code;

        Status(String code) {
            this.code = code;
        }

        @Nonnull
        public String getCode() {
            return code;
        }
    }
}

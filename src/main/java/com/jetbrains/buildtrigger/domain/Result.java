package com.jetbrains.buildtrigger.domain;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Класс для сохранения результатов действия с возможным неуспешным завершением.
 *
 * @param <ResultT> тип результата в случае успешного выполнения
 * @param <ErrorT> тип ошибки
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public class Result<ResultT, ErrorT> {

    /**
     * Данные успешного действия
     */
    @Nullable
    private final ResultT result;

    /**
     * Данные выполнения с ошибкой
     */
    @Nullable
    private final ErrorT error;

    /**
     * Является ли результат выполнения успешным
     */
    private final boolean isSuccess;

    private Result(@Nullable ResultT result,
                   @Nullable ErrorT error,
                   boolean isSuccess) {
        if (result != null && error != null) {
            throw new IllegalArgumentException("both result and error are present");
        }
        this.result = result;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    /**
     * Создать объект, описывающий успешный результат.
     *
     * @param result оборачиваемый объект, являющийся фактическим результатом успешного выполнения метода
     * @param <ResultT> тип результата
     * @param <ErrorT> тип ошибки
     * @return сформированный объект-обёртка результата выполнения метода
     */
    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> success(@Nonnull ResultT result) {
        return new Result<>(result, null, true);
    }

    /**
     * Создать объект обёртки над результатом выполнения метода, подразумевающий, что метод выполнился успешно,
     * несмотря на то, что никакого объекта не сгенерировано
     *
     * @param <ErrorT> тип ошибки
     * @return сформированный объект-обёртка результата выполнения метода
     */
    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> successEmpty() {
        return new Result<>(null, null, true);
    }

    /**
     * Создать объект, описывающий ошибочное выполнение метода
     *
     * @param error объект, представляющий ошибку, возникшую при выполнении
     * @param <ResultT> тип результата
     * @param <ErrorT> тип ошибки
     * @return сформированный объект-обертка результата выполнения метода
     */
    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> error(@Nonnull ErrorT error) {
        return new Result<>(null, error, false);
    }

    /**
     * Создать объект, описывающий ошибочное выполнение метода, не поясняемый объектами ошибки
     *
     * @param <ResultT> тип результата
     * @return сформированный объект-обертка результата выполнения метода
     */
    @Nonnull
    public static <ResultT, ErrorT> Result<ResultT, ErrorT> errorEmpty() {
        return new Result<>(null, null, false);
    }

    /**
     * Является ли результат выполнения - ошибкой
     *
     * @return true, если текущий объект представляет ошибочный результат
     */
    public boolean isError() {
        return !isSuccess;
    }

    /**
     * Получить успешный результат
     *
     * @return успешный результат выполнения, либо {@link Optional#empty()}
     */
    @Nonnull
    public Optional<ResultT> getResult() {
        return Optional.ofNullable(result);
    }

    /**
     * Получить успешный результат.
     * В случае отсутствия будет брошено исключение.
     *
     * @return успешный результат выполнения
     */
    @Nonnull
    public ResultT getResultOrThrow() {
        return getResult().orElseThrow();
    }

    /**
     * Получить ошибку выполнения
     *
     * @return ошибка выполнения метода, либо {@link Optional#empty()}
     */
    @Nonnull
    public Optional<ErrorT> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("result", result)
                .add("error", error)
                .add("isSuccess", isSuccess)
                .toString();
    }
}

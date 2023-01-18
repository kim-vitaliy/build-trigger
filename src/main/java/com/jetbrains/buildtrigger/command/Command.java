package com.jetbrains.buildtrigger.command;

/**
 * Общий интерфейс команды компонента.
 * На вход передаётся DTO запроса, на выходе — данные результата.
 *
 * @param <RequestT> тип класса запроса
 * @param <SuccessT> ип класса результата успеха
 * @param <ErrorT>> тип класса результата ошибки бизнес логики
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
public interface Command<RequestT, SuccessT, ErrorT> {

    /**
     * Выполнить команду.
     *
     * @param request объект запроса
     * @return результат выполнения команды
     */
    CommandResult<SuccessT, ErrorT> execute(RequestT request);
}

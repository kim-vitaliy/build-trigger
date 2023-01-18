package com.jetbrains.buildtrigger.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Исполнитель команд в общем формате
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Component
public class CommandExecutor {

    private final ExecutorService executor;
    private final CommandResultConverter commandResultConverter;

    @Autowired
    public CommandExecutor(ThreadPoolExecutor commandThreadPoolExecutor,
                           CommandResultConverter commandResultConverter) {
        this.executor = commandThreadPoolExecutor;
        this.commandResultConverter = commandResultConverter;
    }

    /**
     * Метод выполняющий команду, на заданном объекте запроса.
     *
     * @param command команда-обработчик
     * @param request данные запроса
     * @param <RequestT> тип запроса
     * @param <SuccessT> тип класса результата успеха
     * @param <ErrorT>> тип класса результата ошибки бизнес логики
     * @return результат, готовый для отдачи из контроллера
     */
    public <RequestT, SuccessT, ErrorT> DeferredResult<ResponseEntity<?>> executeCommand(
            @Nonnull Command<RequestT, SuccessT, ErrorT> command,
            @Nonnull RequestT request) {

        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();
        executor.execute(() -> {
            try {
                ResponseEntity<?> response = commandResultConverter.convertToResponse(command.execute(request));
                deferredResult.setResult(response);
            } catch (Throwable throwable) {
                deferredResult.setErrorResult(throwable);
            }
        });
        return deferredResult;
    }
}

package com.jetbrains.buildtrigger.command;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Конвертер результатов выполнения команды в общий формат ответа.
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Component
public class CommandResultConverter {

    /**
     * Преобразовать результат выполнения команды в ответ эндпоинта на запрос
     *
     * @param result результат выполнения команды
     * @return ответ эндпоинта
     */
    @Nonnull
    public <SuccessT, ErrorT> ResponseEntity<?> convertToResponse(@Nonnull CommandResult<SuccessT, ErrorT> result) {
        switch (result.getStatus()) {
            case SUCCESS:
                return new ResponseEntity<>(result.getSuccess().orElse(null), null, HttpStatus.OK);
            case ERROR:
                return new ResponseEntity<>(result.getError().orElseThrow(), null, HttpStatus.UNPROCESSABLE_ENTITY);

            default:
                throw new IllegalStateException("Unsupported status: " + result.getStatus());
        }
    }
}

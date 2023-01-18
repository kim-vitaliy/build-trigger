package com.jetbrains.buildtrigger.trigger;

import com.jetbrains.buildtrigger.command.CommandExecutor;
import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.command.CreateBuildTriggerCommand;
import com.jetbrains.buildtrigger.trigger.command.DeleteBuildTriggerCommand;
import com.jetbrains.buildtrigger.trigger.command.GetBuildTriggerByIdCommand;
import com.jetbrains.buildtrigger.trigger.command.UpdateBuildTriggerCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.Valid;

/**
 * Контроллер для работы с триггерами
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@RestController
@RequestMapping("/trigger")
public class TriggerController {

    private final CommandExecutor commandExecutor;
    private final CreateBuildTriggerCommand createBuildTriggerCommand;
    private final UpdateBuildTriggerCommand updateBuildTriggerCommand;
    private final GetBuildTriggerByIdCommand getBuildTriggerByIdCommand;
    private final DeleteBuildTriggerCommand deleteBuildTriggerCommand;

    @Autowired
    public TriggerController(CommandExecutor commandExecutor,
                             CreateBuildTriggerCommand createBuildTriggerCommand,
                             UpdateBuildTriggerCommand updateBuildTriggerCommand,
                             GetBuildTriggerByIdCommand getBuildTriggerByIdCommand,
                             DeleteBuildTriggerCommand deleteBuildTriggerCommand) {
        this.commandExecutor = commandExecutor;
        this.createBuildTriggerCommand = createBuildTriggerCommand;
        this.updateBuildTriggerCommand = updateBuildTriggerCommand;
        this.getBuildTriggerByIdCommand = getBuildTriggerByIdCommand;
        this.deleteBuildTriggerCommand = deleteBuildTriggerCommand;
    }

    /**
     * Получить данные триггера по идентификатору
     *
     * @param id уникальный идентификатор триггера
     * @return данные триггера
     */
    @GetMapping("/{id}")
    public DeferredResult<ResponseEntity<?>> getById(@PathVariable Long id) {
        return commandExecutor.executeCommand(getBuildTriggerByIdCommand, id);
    }

    /**
     * Создать новый триггер сборок
     *
     * @param request запрос на создание триггера
     * @return ответ на запрос
     */
    @PostMapping("/create")
    public DeferredResult<ResponseEntity<?>> create(@RequestBody @Valid CreateBuildTriggerRequest request) {
        return commandExecutor.executeCommand(createBuildTriggerCommand, request);
    }

    /**
     * Обновить триггер сборок
     *
     * @param request запрос на обновление триггера
     * @return ответ на запрос
     */
    @PutMapping("/update")
    public DeferredResult<ResponseEntity<?>> update(@RequestBody @Valid UpdateBuildTriggerRequest request) {
        return commandExecutor.executeCommand(updateBuildTriggerCommand, request);
    }

    /**
     * Удалить триггер сборок
     *
     * @param id уникальный идентификатор триггера
     * @return ответ на запрос
     */
    @DeleteMapping("/{id}")
    public DeferredResult<ResponseEntity<?>> delete(@PathVariable Long id) {
        return commandExecutor.executeCommand(deleteBuildTriggerCommand, id);
    }
}

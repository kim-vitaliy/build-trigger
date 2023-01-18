package com.jetbrains.buildtrigger.trigger.service;

import com.jetbrains.buildtrigger.trigger.api.create.CreateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.getbyid.GetBuildTriggerByIdResponse;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerRequest;
import com.jetbrains.buildtrigger.trigger.api.update.UpdateBuildTriggerResponse;
import com.jetbrains.buildtrigger.trigger.domain.Branch;
import com.jetbrains.buildtrigger.trigger.domain.BuildTrigger;
import com.jetbrains.buildtrigger.trigger.domain.ExecutionByTimeData;
import com.jetbrains.buildtrigger.trigger.domain.RepositoryData;
import com.jetbrains.buildtrigger.trigger.domain.ScheduledTriggerData;
import com.jetbrains.buildtrigger.trigger.domain.VcsTriggerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 * Преобразователь данных из доменного представления в API и наоборот
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
@Component
public class BuildTriggerApiMapper {

    private final BuildTriggerApiEnumsMapper buildTriggerApiEnumsMapper;

    @Autowired
    public BuildTriggerApiMapper(BuildTriggerApiEnumsMapper buildTriggerApiEnumsMapper) {
        this.buildTriggerApiEnumsMapper = buildTriggerApiEnumsMapper;
    }

    /**
     * Создать доменный объект триггера из запроса на создание триггера: {@link CreateBuildTriggerRequest}
     *
     * @param request запрос на создание триггера в терминах API
     * @param creationDate дата создания триггера
     * @return триггер в доменном представлении
     */
    @Nonnull
    public BuildTrigger buildFromCreateRequest(@Nonnull CreateBuildTriggerRequest request,
                                               @Nonnull ZonedDateTime creationDate) {

        return BuildTrigger.builder()
                .withType(buildTriggerApiEnumsMapper.mapTriggerTypeFromApi(request.getTriggerType()))
                .withCreated(creationDate)
                .withUpdated(creationDate)
                .withRepositoryData(RepositoryData.builder()
                        .withRepositoryUrl(request.getRepositoryData().getRepositoryUrl())
                        .withUsername(request.getRepositoryData().getUsername())
                        .withPassword(request.getRepositoryData().getPassword())
                        .build())
                .withBranches(request.getBranches().stream()
                        .map(branch -> Branch.builder()
                                .withBranchName(branch)
                                .build())
                        .collect(Collectors.toSet()))
                .withScheduledTriggerData(request.getScheduledTriggerData()
                        .map(scheduledTriggerData -> {
                            var executionByTimeData = scheduledTriggerData.getExecutionByTimeData();
                            return ScheduledTriggerData.builder()
                                    .withExecutionByTimeData(ExecutionByTimeData.builder()
                                            .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeFromApi(executionByTimeData.getIntervalType()))
                                            .withCron(executionByTimeData.getCron().orElse(null))
                                            .withFixedRateInterval(executionByTimeData.getFixedRateInterval().orElse(null))
                                            .build())
                                    .build();
                        })
                        .orElse(null))
                .withVcsTriggerData(request.getVcsTriggerData()
                        .map(vcsTriggerData -> {
                            var executionByTimeData = vcsTriggerData.getExecutionByTimeData();
                            var builder = VcsTriggerData.builder()
                                    .withSynchronizationMode(buildTriggerApiEnumsMapper.mapSynchronizationModeFromApi(vcsTriggerData.getSynchronizationMode()));

                            executionByTimeData.ifPresent(byTimeData -> builder.withExecutionByTimeData(ExecutionByTimeData.builder()
                                    .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeFromApi(byTimeData.getIntervalType()))
                                    .withCron(byTimeData.getCron().orElse(null))
                                    .withFixedRateInterval(byTimeData.getFixedRateInterval().orElse(null))
                                    .build()));
                            return builder.build();
                        })
                        .orElse(null))
                .build();
    }

    /**
     * Создать доменный объект триггера из запроса на обновление триггера: {@link UpdateBuildTriggerRequest}
     *
     * @param request запрос на обновление триггера в терминах API
     * @param triggerToUpdate триггер, который необходимо обновить
     * @param updateDate дата обновления триггера
     * @return триггер в доменном представлении
     */
    @Nonnull
    public BuildTrigger buildFromUpdateRequest(@Nonnull UpdateBuildTriggerRequest request,
                                               @Nonnull BuildTrigger triggerToUpdate,
                                               @Nonnull ZonedDateTime updateDate) {

        return BuildTrigger.builder()
                .withId(triggerToUpdate.getId())
                .withType(buildTriggerApiEnumsMapper.mapTriggerTypeFromApi(request.getTriggerType()))
                .withCreated(triggerToUpdate.getCreated())
                .withUpdated(updateDate)
                .withRepositoryData(RepositoryData.builder()
                        .withRepositoryUrl(request.getRepositoryData().getRepositoryUrl())
                        .withUsername(request.getRepositoryData().getUsername())
                        .withPassword(request.getRepositoryData().getPassword())
                        .build())
                .withBranches(request.getBranches().stream()
                        .map(branch -> Branch.builder()
                                .withBranchName(branch)
                                .build())
                        .collect(Collectors.toSet()))
                .withScheduledTriggerData(request.getScheduledTriggerData()
                        .map(scheduledTriggerData -> {
                            var executionByTimeData = scheduledTriggerData.getExecutionByTimeData();
                            return ScheduledTriggerData.builder()
                                    .withExecutionByTimeData(ExecutionByTimeData.builder()
                                            .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeFromApi(executionByTimeData.getIntervalType()))
                                            .withCron(executionByTimeData.getCron().orElse(null))
                                            .withFixedRateInterval(executionByTimeData.getFixedRateInterval().orElse(null))
                                            .build())
                                    .build();
                        })
                        .orElse(null))
                .withVcsTriggerData(request.getVcsTriggerData()
                        .map(vcsTriggerData -> {
                            var executionByTimeData = vcsTriggerData.getExecutionByTimeData();
                            var builder = VcsTriggerData.builder()
                                    .withSynchronizationMode(buildTriggerApiEnumsMapper.mapSynchronizationModeFromApi(vcsTriggerData.getSynchronizationMode()));

                            executionByTimeData.ifPresent(byTimeData -> builder.withExecutionByTimeData(ExecutionByTimeData.builder()
                                    .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeFromApi(byTimeData.getIntervalType()))
                                    .withCron(byTimeData.getCron().orElse(null))
                                    .withFixedRateInterval(byTimeData.getFixedRateInterval().orElse(null))
                                    .build()));
                            return builder.build();
                        })
                        .orElse(null))
                .build();
    }

    /**
     * Преобразовать триггер в ответ на запрос данных триггера по идентификатору
     *
     * @param trigger триггер в доменном представлении
     * @return триггер в терминах API
     */
    @Nonnull
    public GetBuildTriggerByIdResponse mapToGetByIdResponse(@Nonnull BuildTrigger trigger) {
        return GetBuildTriggerByIdResponse.builder()
                .withId(trigger.getId())
                .withTriggerType(buildTriggerApiEnumsMapper.mapTriggerTypeToApi(trigger.getType()))
                .withRepositoryData(com.jetbrains.buildtrigger.trigger.api.RepositoryData.builder()
                        .withRepositoryUrl(trigger.getRepositoryData().getRepositoryUrl())
                        .withUsername(trigger.getRepositoryData().getUsername())
                        .withPassword(trigger.getRepositoryData().getPassword())
                        .build())
                .withBranches(trigger.getBranches().stream().map(Branch::getBranchName).collect(Collectors.toSet()))
                .withNextExecutionTime(trigger.getNextExecutionTime().orElse(null))
                .withCreated(trigger.getCreated())
                .withUpdated(trigger.getUpdated())
                .withVcsTriggerData(trigger.getVcsTriggerData()
                        .map(vcsTriggerData -> com.jetbrains.buildtrigger.trigger.api.VcsTriggerData.builder()
                                .withSynchronizationMode(buildTriggerApiEnumsMapper.mapSynchronizationModeToApi(vcsTriggerData.getSynchronizationMode()))
                                .withExecutionByTimeData(vcsTriggerData.getExecutionByTimeData()
                                        .map(executionByTimeData -> com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData.builder()
                                                .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeToApi(executionByTimeData.getIntervalType()))
                                                .withFixedRateInterval(executionByTimeData.getFixedRateInterval().orElse(null))
                                                .withCron(executionByTimeData.getCron().orElse(null))
                                                .build())
                                        .orElse(null))
                                .build())
                        .orElse(null))
                .withScheduledTriggerData(trigger.getScheduledTriggerData()
                        .map(scheduledTriggerData -> com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData.builder()
                                .withExecutionByTimeData(com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData.builder()
                                        .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeToApi(scheduledTriggerData.getExecutionByTimeData().getIntervalType()))
                                        .withFixedRateInterval(scheduledTriggerData.getExecutionByTimeData().getFixedRateInterval().orElse(null))
                                        .withCron(scheduledTriggerData.getExecutionByTimeData().getCron().orElse(null))
                                        .build())
                                .build())
                        .orElse(null))
                .build();
    }

    /**
     * Преобразовать триггер в ответ на запрос на обнолвение триггера
     *
     * @param trigger триггер в доменном представлении
     * @return триггер в терминах API
     */
    @Nonnull
    public UpdateBuildTriggerResponse mapToUpdateResponse(@Nonnull BuildTrigger trigger) {
        return UpdateBuildTriggerResponse.builder()
                .withId(trigger.getId())
                .withTriggerType(buildTriggerApiEnumsMapper.mapTriggerTypeToApi(trigger.getType()))
                .withRepositoryData(com.jetbrains.buildtrigger.trigger.api.RepositoryData.builder()
                        .withRepositoryUrl(trigger.getRepositoryData().getRepositoryUrl())
                        .withUsername(trigger.getRepositoryData().getUsername())
                        .withPassword(trigger.getRepositoryData().getPassword())
                        .build())
                .withBranches(trigger.getBranches().stream().map(Branch::getBranchName).collect(Collectors.toSet()))
                .withNextExecutionTime(trigger.getNextExecutionTime().orElse(null))
                .withCreated(trigger.getCreated())
                .withUpdated(trigger.getUpdated())
                .withVcsTriggerData(trigger.getVcsTriggerData()
                        .map(vcsTriggerData -> com.jetbrains.buildtrigger.trigger.api.VcsTriggerData.builder()
                                .withSynchronizationMode(buildTriggerApiEnumsMapper.mapSynchronizationModeToApi(vcsTriggerData.getSynchronizationMode()))
                                .withExecutionByTimeData(vcsTriggerData.getExecutionByTimeData()
                                        .map(executionByTimeData -> com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData.builder()
                                                .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeToApi(executionByTimeData.getIntervalType()))
                                                .withFixedRateInterval(executionByTimeData.getFixedRateInterval().orElse(null))
                                                .withCron(executionByTimeData.getCron().orElse(null))
                                                .build())
                                        .orElse(null))
                                .build())
                        .orElse(null))
                .withScheduledTriggerData(trigger.getScheduledTriggerData()
                        .map(scheduledTriggerData -> com.jetbrains.buildtrigger.trigger.api.ScheduledTriggerData.builder()
                                .withExecutionByTimeData(com.jetbrains.buildtrigger.trigger.api.ExecutionByTimeData.builder()
                                        .withIntervalType(buildTriggerApiEnumsMapper.mapIntervalTypeToApi(scheduledTriggerData.getExecutionByTimeData().getIntervalType()))
                                        .withFixedRateInterval(scheduledTriggerData.getExecutionByTimeData().getFixedRateInterval().orElse(null))
                                        .withCron(scheduledTriggerData.getExecutionByTimeData().getCron().orElse(null))
                                        .build())
                                .build())
                        .orElse(null))
                .build();
    }
}

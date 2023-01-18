package com.jetbrains.buildtrigger.vcs;

import com.jetbrains.buildtrigger.trigger.domain.RepositoryData;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Сервис выполнения операций с git-репозиторием
 *
 * @author Vitaliy Kim
 * @since 19.01.2023
 */
@Component
public class GitManager {

    private static final Logger log = LoggerFactory.getLogger(GitManager.class);

    /**
     * Получить ветки из удалённого репозитория
     *
     * @param repositoryData данные подключения к удалённому репозиторию
     * @return список веток
     */
    public Collection<Ref> fetchBranchesFromRemote(@Nonnull RepositoryData repositoryData) {
        log.info("fetchBranchesFromRemote(): repository={}, username={}", repositoryData.getRepositoryUrl(), repositoryData.getUsername());

        try {
            return Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(repositoryData.getRepositoryUrl())
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            repositoryData.getUsername(),
                            repositoryData.getPassword()))
                    .call();

        } catch (Exception e) {
            log.warn("Error while trying to connect to remote repository: repository={}", repositoryData.getRepositoryUrl(), e);
            return Collections.emptyList();
        }
    }
}

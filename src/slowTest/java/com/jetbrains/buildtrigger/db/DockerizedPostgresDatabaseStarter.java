package com.jetbrains.buildtrigger.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Запускает БД postgres в Docker-контейнере.
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public class DockerizedPostgresDatabaseStarter {

    private static final Logger log = LoggerFactory.getLogger(DockerizedPostgresDatabaseStarter.class);

    private Set<String> initializedDatabases = new HashSet<>();
    private PostgreSQLContainer<?> dbContainer;

    private DockerizedPostgresDatabaseStarter() {
    }

    public static DockerizedPostgresDatabaseStarter getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void initDatabase(InitDatabaseSettings settings) {
        requireNonNull(settings, "settings");

        startDatabaseContainer(settings);
        setupDatabases(settings);
    }

    private void startDatabaseContainer(@Nonnull InitDatabaseSettings settings) {
        if (dbContainer != null) {
            log.info("Container has already initialized");
            return;
        }

        dbContainer = new PostgreSQLContainer<>(DockerImageName
                .parse(buildDockerImageName(settings))
                .asCompatibleSubstituteFor("postgres"));
        dbContainer.withEnv("POSTGRES_INITDB_ARGS", "--nosync");
        dbContainer.withCommand("postgres " +
                "-c fsync=off " +
                "-c full_page_writes=off " +
                "-c synchronous_commit=off");
        dbContainer.start();
    }

    /**
     * Формирует имя docker-образа для сборочного окружения
     */
    private String buildDockerImageName(InitDatabaseSettings settings) {
        return PostgreSQLContainer.IMAGE + ":" + settings.getPostgresImageTag();
    }

    private void setupDatabases(InitDatabaseSettings settings) {
        settings.getDatabases().forEach(it -> {
                    if (initializedDatabases.add(it)) {
                        setupDatabase(it);
                        log.info("Database created: dbName={}", it);
                    } else {
                        log.warn("Database already created: dbName={}", it);
                    }
                }
        );
    }

    private void setupDatabase(String dbName) {
        createDatabase(dbName);
        setConnectionsProperties();
    }

    private void createDatabase(String dbName) {
        String user = dbContainer.getUsername();
        try (Connection c = dbContainer.createConnection("")) {
            try (PreparedStatement stmt = c.prepareStatement(
                String.format("DO " +
                                  "$do$ " +
                                  "BEGIN " +
                                  "   IF NOT EXISTS ( " +
                                  "      SELECT " +
                                  "      FROM   pg_catalog.pg_roles " +
                                  "      WHERE  rolname = '%s') THEN " +
                                  "      CREATE ROLE %s superuser createdb login;" +
                                  "   END IF; " +
                                  "END " +
                                  "$do$;" +
                                  "" +
                                  "CREATE DATABASE %s OWNER %s ENCODING = 'utf8'; ",
                              user, user, dbName, user
                )
            )) {
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setConnectionsProperties() {
        System.setProperty("db.master.url", dbContainer.getJdbcUrl());
        System.setProperty("db.master.username", dbContainer.getUsername());
        System.setProperty("db.master.password", dbContainer.getPassword());
    }

    private static final class SingletonHolder {
        private static final DockerizedPostgresDatabaseStarter INSTANCE = new DockerizedPostgresDatabaseStarter();
    }
}

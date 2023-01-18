package com.jetbrains.buildtrigger.config;

import com.jetbrains.buildtrigger.config.properties.DatasourceMasterProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация подключения к БД
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@Configuration
public class DatasourceConfiguration {

    @Bean
    public HikariDataSource hikariDataSource(DatasourceMasterProperties datasourceMasterProperties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(datasourceMasterProperties.getUrl());
        hikariConfig.setUsername(datasourceMasterProperties.getUsername());
        hikariConfig.setPassword(datasourceMasterProperties.getPassword());
        hikariConfig.setDriverClassName(datasourceMasterProperties.getDriverClassName());
        hikariConfig.setPoolName(datasourceMasterProperties.getPoolName());
        hikariConfig.setMaximumPoolSize(datasourceMasterProperties.getMaxPoolSize());
        hikariConfig.setMinimumIdle(datasourceMasterProperties.getMinimumIdle());
        hikariConfig.setConnectionTimeout(datasourceMasterProperties.getConnectionTimeoutMs());
        hikariConfig.setAutoCommit(false);
        hikariConfig.addDataSourceProperty("socketTimeout", datasourceMasterProperties.getSocketTimeoutMs());

        return new HikariDataSource(hikariConfig);
    }
}

package com.jetbrains.buildtrigger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Nonnull;

/**
 * Настройки подключения к БД
 *
 * @author Vitaliy Kim
 * @since 17.01.2023
 */
@PropertySource("classpath:config/datasource-master.properties")
@ConfigurationProperties(prefix = "db.master")
@Configuration
public class DatasourceMasterProperties {

    /**
     * Адрес БД
     */
    private String url;

    /**
     * Имя пользователя
     */
    private String username;

    /**
     * Пароль
     */
    private String password;

    /**
     * Название драйвера БД
     */
    private String driverClassName;

    /**
     * Наименование пула соединений с БД
     */
    private String poolName;

    /**
     * Максимальный размер пула соединений с БД
     */
    private Integer maxPoolSize;

    /**
     * Минимальное количество соединений в пуле, которые могут бездействовать
     */
    private Integer minimumIdle;

    /**
     * Максимальное время, в течение которого может быть установлено соединение к БД
     */
    private Long connectionTimeoutMs;

    /**
     * Максимальное время, в течение которого может быть выполнен запрос к БД
     */
    private Long socketTimeoutMs;

    @Nonnull
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nonnull String url) {
        this.url = url;
    }

    @Nonnull
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nonnull String username) {
        this.username = username;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }

    @Nonnull
    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(@Nonnull String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Nonnull
    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(@Nonnull String poolName) {
        this.poolName = poolName;
    }

    @Nonnull
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(@Nonnull Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    @Nonnull
    public Integer getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(@Nonnull Integer minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    @Nonnull
    public Long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(@Nonnull Long connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    @Nonnull
    public Long getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(@Nonnull Long socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }
}

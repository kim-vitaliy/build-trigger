package com.jetbrains.buildtrigger.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Nonnull;

/**
 * Настройки RabbitMQ
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
@PropertySource("classpath:config/rabbit-mq.properties")
@ConfigurationProperties(prefix = "rabbit-mq")
@Configuration
public class RabbitMqProperties {

    /**
     * Адрес RabbitMQ
     */
    private String url;

    /**
     * Логин
     */
    private String login;

    /**
     * Пароль
     */
    private String password;

    /**
     * Настройки маршрутизации события триггера билда
     */
    private RabbitMqBindingSettings buildTriggered;

    @Nonnull
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Nonnull
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nonnull
    public RabbitMqBindingSettings getBuildTriggered() {
        return buildTriggered;
    }

    public void setBuildTriggered(@Nonnull RabbitMqBindingSettings buildTriggered) {
        this.buildTriggered = buildTriggered;
    }
}

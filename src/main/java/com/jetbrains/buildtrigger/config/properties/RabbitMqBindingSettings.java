package com.jetbrains.buildtrigger.config.properties;

/**
 * Настройки роутинга Rabbit MQ
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
public class RabbitMqBindingSettings {

    /**
     * Название exchange
     */
    private String exchange;

    /**
     * Название очереди
     */
    private String queue;

    /**
     * Ключ маршрутизации
     */
    private String routingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}

package com.jetbrains.buildtrigger.config;

import com.jetbrains.buildtrigger.config.properties.RabbitMqProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Rabbit MQ.
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
@Configuration
public class RabbitMqConfiguration {

    @Bean
    public TopicExchange buildTriggeredExchange(RabbitMqProperties rabbitMqProperties) {
        return new TopicExchange(rabbitMqProperties.getBuildTriggered().getExchange());
    }

    @Bean
    public Queue buildsMainQueue(RabbitMqProperties rabbitMqProperties) {
        return new Queue(rabbitMqProperties.getBuildTriggered().getQueue());
    }

    @Bean
    public Binding buildTriggeredBinding(Queue buildsMainQueue,
                                         TopicExchange buildTriggeredExchange,
                                         RabbitMqProperties rabbitMqProperties) {
        return BindingBuilder.bind(buildsMainQueue)
                .to(buildTriggeredExchange)
                .with(rabbitMqProperties.getBuildTriggered().getRoutingKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2Converter1() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory(RabbitMqProperties rabbitMqProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(rabbitMqProperties.getUrl());
        connectionFactory.setUsername(rabbitMqProperties.getLogin());
        connectionFactory.setPassword(rabbitMqProperties.getPassword());

        return connectionFactory;
    }
}

package com.jetbrains.buildtrigger.trigger.producer;

import com.jetbrains.buildtrigger.config.properties.RabbitMqProperties;
import com.jetbrains.buildtrigger.domain.Result;
import com.jetbrains.buildtrigger.trigger.producer.message.BuildTriggeredMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * Продьюсер события триггера сборки
 *
 * @author Vitaliy Kim
 * @since 20.01.2023
 */
@Component
public class BuildTriggeredEventProducer {

    private static final Logger log = LoggerFactory.getLogger(BuildTriggeredEventProducer.class);

    /**
     * В данной реализации: {@link TopicExchange} предполагается, что он будет динамическим.
     */
    private static final String ROUTING_KEY = "build.triggered.default";

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    @Autowired
    public BuildTriggeredEventProducer(RabbitTemplate rabbitTemplate,
                                       RabbitMqProperties rabbitMqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = rabbitMqProperties.getBuildTriggered().getExchange();
    }

    /**
     * Отправить сообщение о триггере билда в MQ.
     *
     * @param message данные сообщения
     * @return результат отправки
     */
    public Result<Void, Void> pushBuildTriggeredMessage(@Nonnull BuildTriggeredMessage message) {
        log.info("pushBuildTriggeredMessage(): message={}", message);

        try {
            rabbitTemplate.convertAndSend(exchange, ROUTING_KEY, message);
            return Result.successEmpty();
        } catch (Exception e) {
            log.warn("Error while trying to connect to Rabbit MQ", e);
            return Result.errorEmpty();
        }
    }
}

package com.jetbrains.buildtrigger.http;

import javax.annotation.Nonnull;
import java.time.Duration;

import static java.util.Objects.requireNonNull;

/**
 * Настройки http-клиента, используемые в {@link HttpClientFactory}
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public final class ClientParams {
    /**
     * Имя клиента
     */
    @Nonnull
    private final String clientName;

    /**
     * Максимальное количество соединений клиентом
     */
    private final int maxConnections;

    /**
     * Максимальное количество соединений клиентом к одному хосту
     */
    private final int maxConnectionsPerRoute;

    /**
     * Таймаут передачи данных
     */
    @Nonnull
    private final Duration socketTimeout;

    /**
     * Таймаут установления соединения с хостом
     */
    @Nonnull
    private final Duration connectTimeout;

    /**
     * Время ожидания соединения из пула.
     * Увеличить количество соединений, если получен ConnectionPoolTimeoutException.
     * При значительном таймауте, увеличить количество соединений.
     */
    @Nonnull
    private final Duration waitConnectionFromPoolTimeout;

    /**
     * Максимальное время жизни http-соединения
     */
    @Nonnull
    private final Duration connectionTimeToLive;

    private ClientParams(@Nonnull String clientName,
                         int maxConnections,
                         int maxConnectionsPerRoute,
                         @Nonnull Duration socketTimeout,
                         @Nonnull Duration connectTimeout,
                         @Nonnull Duration waitConnectionFromPoolTimeout,
                         @Nonnull Duration connectionTimeToLive) {
        this.clientName = requireNonNull(clientName, "clientName");
        this.maxConnections = maxConnections;
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
        this.socketTimeout = requireNonNull(socketTimeout, "socketTimeout");
        this.connectTimeout = requireNonNull(connectTimeout, "connectTimeout");
        this.waitConnectionFromPoolTimeout = requireNonNull(waitConnectionFromPoolTimeout, "waitConnectionFromPoolTimeout");
        this.connectionTimeToLive = requireNonNull(connectionTimeToLive, "connectionTimeToLive");
    }

    @Nonnull
    public String getClientName() {
        return clientName;
    }

    int getMaxConnections() {
        return maxConnections;
    }

    int getMaxConnectionsPerRoute() {
        return maxConnectionsPerRoute;
    }

    @Nonnull
    Duration getSocketTimeout() {
        return socketTimeout;
    }

    @Nonnull
    Duration getConnectTimeout() {
        return connectTimeout;
    }

    @Nonnull
    public Duration getWaitConnectionFromPoolTimeout() {
        return waitConnectionFromPoolTimeout;
    }

    @Nonnull
    public Duration getConnectionTimeToLive() {
        return connectionTimeToLive;
    }

    /**
     * Создает новый объект билдера для {@link ClientParams}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Билдер {@link ClientParams}
     */
    public static class Builder {
        private String clientName;
        private int maxConnections = 10;
        private int maxConnectionsPerRoute = 10;
        private Duration socketTimeout = Duration.ofMillis(5000);
        private Duration connectTimeout = Duration.ofMillis(500);
        private Duration waitConnectionFromPoolTimeout = Duration.ofMillis(5000);
        private Duration connectionTimeToLive = Duration.ofMillis(600000);

        public Builder setClientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
            this.maxConnectionsPerRoute = maxConnectionsPerRoute;
            return this;
        }

        public Builder setSocketTimeout(@Nonnull Duration socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setConnectionTimeToLive(@Nonnull Duration connectionTimeToLive) {
            this.connectionTimeToLive = connectionTimeToLive;
            return this;
        }

        public Builder setConnectTimeout(@Nonnull Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setWaitConnectionFromPoolTimeout(@Nonnull Duration timeout) {
            this.waitConnectionFromPoolTimeout = timeout;
            return this;
        }

        /**
         * Собирает {@link ClientParams}
         *
         * @return собранные настройки http клиента
         * @throws IllegalArgumentException в случае недопустимых параметров
         */
        public ClientParams build() throws IllegalArgumentException {
            if (maxConnections <= 0) {
                throw new IllegalArgumentException("Max connections must be more than 0 (" + maxConnections + ')');
            }
            checkTimeoutValue(socketTimeout, "Socket timeout");
            checkTimeoutValue(connectTimeout, "Connect timeout");
            checkTimeoutValue(waitConnectionFromPoolTimeout, "Connection request timeout");
            checkTimeoutValue(connectionTimeToLive, "Connection time to live");

            return new ClientParams(
                    clientName,
                    maxConnections,
                    maxConnectionsPerRoute,
                    socketTimeout,
                    connectTimeout,
                    waitConnectionFromPoolTimeout,
                    connectionTimeToLive
            );
        }

        private static void checkTimeoutValue(Duration socketTimeout, String timeoutName) {
            if (socketTimeout.isNegative() || socketTimeout.isZero()) {
                throw new IllegalArgumentException(timeoutName + " must be more than 0 ms (" + socketTimeout + ')');
            }
        }
    }
}
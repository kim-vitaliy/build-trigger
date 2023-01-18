package com.jetbrains.buildtrigger.http;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Фабрика создания HttpClient
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
@Component
public class HttpClientFactory {

    /**
     * Создать http-клиент
     *
     * @return экземпляр http-клиента
     */
    @Nonnull
    public CloseableHttpClient createHttpClient(ClientParams clientParams) {
        var httpClientBuilder = HttpClientBuilder.create();

        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(StandardCookieSpec.STRICT)
                .setConnectTimeout(clientParams.getConnectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(clientParams.getWaitConnectionFromPoolTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(clientParams.getSocketTimeout().toMillis()))
                .setTcpNoDelay(true)
                .build();

        PoolingHttpClientConnectionManager poolingMgr = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
                        .register(URIScheme.HTTPS.id, SSLConnectionSocketFactory.getSocketFactory())
                        .build(), null, null,
                TimeValue.ofMilliseconds(clientParams.getConnectionTimeToLive().toMillis()),
                null, null, null
        );

        poolingMgr.setDefaultSocketConfig(socketConfig);
        poolingMgr.setMaxTotal(clientParams.getMaxConnections());
        poolingMgr.setDefaultMaxPerRoute(clientParams.getMaxConnectionsPerRoute());

        httpClientBuilder.setConnectionManager(poolingMgr);

        return httpClientBuilder.build();
    }

}

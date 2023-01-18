package com.jetbrains.buildtrigger.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockServiceProvider {

    private static final Logger log = LoggerFactory.getLogger(WireMockServiceProvider.class);

    /**
     * Создает конфигурацию со значениями по умолчанию
     *
     * @return {@link WireMockConfiguration}
     */
    public static WireMockConfiguration buildConfig() {
        return options()
                .extensions(new ToStringResponseTransformer(), new ImperativeResponseTransformer())
                .containerThreads(40)
                .maxRequestJournalEntries(1_000);
    }

    /**
     * Создает инстанс сервиса {@link WireMockServer} и прогревает его
     *
     * @param options опции полученные из {@link #buildConfig()}
     * @return {@link WireMockServer}
     */
    public static WireMockServer wireMockServer(WireMockConfiguration options) {
        WireMockServer wireMockServer = new WireMockServer(options);
        wireMockServer.start();
        WireMock.configureFor("localhost", options.portNumber());
        log.info("warmUp started: port={}", options.portNumber());

        try {
            StubMapping stubMapping = wireMockServer.stubFor(get("/").willReturn(aResponse().withStatus(200)));
            sendGetHttpRequest(options);
            wireMockServer.removeStub(stubMapping);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("warmUp end");
        return wireMockServer;
    }

    private static void sendGetHttpRequest(WireMockConfiguration options) throws IOException {
        URL url = new URL("http://localhost:" + options.portNumber() + "/");
        HttpURLConnection httpClient = ((HttpURLConnection) url.openConnection());
        httpClient.setRequestMethod("GET");
        httpClient.getResponseCode();
        httpClient.disconnect();
    }
}

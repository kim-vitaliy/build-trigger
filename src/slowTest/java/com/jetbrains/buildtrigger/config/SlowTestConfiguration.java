package com.jetbrains.buildtrigger.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.Stubbing;
import com.jetbrains.buildtrigger.client.TriggerClient;
import com.jetbrains.buildtrigger.helper.TriggerHelper;
import com.jetbrains.buildtrigger.http.ClientParams;
import com.jetbrains.buildtrigger.http.HttpClientFactory;
import com.jetbrains.buildtrigger.http.JsonEndpointCaller;
import com.jetbrains.buildtrigger.mock.WireMockServiceProvider;
import com.jetbrains.buildtrigger.stub.GitStub;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(SlowTestConfiguration.SLOW_TEST_PROFILE)
public class SlowTestConfiguration {

    public static final String SLOW_TEST_PROFILE = "slow-test";
    public static final String TEST_JETTY_STUBS_PORT = "testJettyStubsPort";
    public static final String SCHEDULING_ENABLED_PROPERTY = "scheduling.enabled";
    private static final String BASE_URL = String.format("http://localhost:%s", System.getProperty("testAppPort"));

    @Bean
    public WireMockConfiguration wireMockOptions() {
        int port = Integer.parseInt(System.getProperty(TEST_JETTY_STUBS_PORT));

        return WireMockServiceProvider.buildConfig()
                .port(port);
    }

    @Bean(destroyMethod = "stop")
    public WireMockServer wireMockServer(WireMockConfiguration options) {
        return WireMockServiceProvider.wireMockServer(options);
    }

    @Bean
    public CloseableHttpClient slowTestHttpClient(HttpClientFactory httpClientFactory) {
        return httpClientFactory.createHttpClient(ClientParams.builder()
                .setClientName("slowTestHttpClient")
                .build());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(MapperFeature.AUTO_DETECT_CREATORS, true)
                .configure(MapperFeature.AUTO_DETECT_FIELDS, false)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_SETTERS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public JsonEndpointCaller jsonEndpointCaller(CloseableHttpClient componentTestHttpClient,
                                                   ObjectMapper objectMapper) {
        return new JsonEndpointCaller(
                componentTestHttpClient,
                BASE_URL,
                objectMapper);
    }

    @Bean
    public TriggerClient triggerClient(JsonEndpointCaller jsonEndpointCaller) {
        return new TriggerClient(jsonEndpointCaller);
    }

    @Bean
    public TriggerHelper triggerHelper() {
        return new TriggerHelper();
    }

    @Bean
    public GitStub gitStub(Stubbing stubbing) {
        return new GitStub(stubbing);
    }
}

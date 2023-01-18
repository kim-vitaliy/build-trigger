package com.jetbrains.buildtrigger;

import com.jetbrains.buildtrigger.config.SlowTestConfiguration;
import com.jetbrains.buildtrigger.db.DockerizedPostgresDatabaseStarter;
import com.jetbrains.buildtrigger.db.InitDatabaseSettings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Базовый класс для компонентных тестов
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
@ActiveProfiles(SlowTestConfiguration.SLOW_TEST_PROFILE)
@SpringBootTest(classes = {BuildTriggerApplication.class, SlowTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractSlowTest extends AbstractTestNGSpringContextTests {

    static {
        DockerizedPostgresDatabaseStarter.getInstance()
                .initDatabase(InitDatabaseSettings.builder()
                        .withPostgresImageTag("11")
                        .addDatabase("slow_test")
                        .build());

        System.setProperty("testAppPort", String.valueOf(getFreeRandomLocalPort()));
        System.setProperty(SlowTestConfiguration.TEST_JETTY_STUBS_PORT, String.valueOf(getFreeRandomLocalPort()));
        System.setProperty(SlowTestConfiguration.SCHEDULING_ENABLED_PROPERTY, "false");
    }

    private static int getFreeRandomLocalPort() {
        try {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

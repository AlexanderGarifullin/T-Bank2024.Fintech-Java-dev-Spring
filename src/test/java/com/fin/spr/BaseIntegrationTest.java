package com.fin.spr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.services.security.AuthenticationService;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withUsername("postgres")
            .withPassword("123")
            .withDatabaseName("testdb");

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AuthenticationService authenticationService;

    protected static String userBearerToken;
    protected static String adminBearerToken;

    protected static final AuthenticationPayload userRequest = new AuthenticationPayload(
            "user",
            "password",
            false
    );

    protected static final AuthenticationPayload adminRequest = new AuthenticationPayload(
            "admin",
            "password",
            false
    );

    private static void runLiquibaseMigrations() throws Exception {
        Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        String changeLogFile = System.getProperty("spring.liquibase.change-log", "db/changelog/db.changelog-test.yaml");

        try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
            liquibase.update(new Contexts());
        }
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) throws Exception {
        postgres.start();
        runLiquibaseMigrations();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }

    @BeforeEach
    public void getToken() throws Exception {
        if (userBearerToken == null) {
            userBearerToken = "Bearer %s".formatted(authenticationService.login(userRequest).token());
        }

        if (adminBearerToken == null) {
            adminBearerToken = "Bearer %s".formatted(authenticationService.login(adminRequest).token());
        }
    }
}

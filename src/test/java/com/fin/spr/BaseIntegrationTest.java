package com.fin.spr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fin.spr.controllers.payload.security.AuthenticationPayload;
import com.fin.spr.services.security.AuthenticationService;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    private PostgreSQLContainer<?> postgreSQLContainer;

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

    void setUp() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgreSQLContainer.start();


        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());

        try (Connection connection = DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        )) {
            runLiquibaseMigrations(connection);
        } catch (LiquibaseException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void runLiquibaseMigrations(Connection connection) throws LiquibaseException {
        // Настраиваем Liquibase для выполнения миграций
        Database database = new liquibase.database.core.PostgresDatabase();
        database.setConnection(new JdbcConnection(connection));

        // Указываем основной файл миграций
        String changeLogFile = System.getProperty("spring.liquibase.change-log", "db/changelog/db.changelog-master.yaml");

        try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
            liquibase.update(new Contexts());
        }
    }

    @BeforeEach
    public void getToken() {
        if (userBearerToken == null) {
            userBearerToken = "Bearer %s".formatted(authenticationService.login(userRequest).token());
        }

        if (adminBearerToken == null) {
            adminBearerToken = "Bearer %s".formatted(authenticationService.login(adminRequest).token());
        }
    }
}

package com.fin.spr;


import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class BaseIntegrationTest {

    private PostgreSQLContainer<?> postgreSQLContainer;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
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
        try (Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml",
                new ClassLoaderResourceAccessor(),
                database)) {
            liquibase.update(new Contexts());
        }
    }
}

package com.w11k.psql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PsqlWorkerJavaTest {
    @Rule
    public PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:10.3-alpine")
            .withDatabaseName("test_database")
            .withUsername("postgres")
            .withPassword("password");

    private PsqlWorker worker;

    @BeforeEach
    public void setupDatasource() {
        postgres.start();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(postgres.getJdbcUrl());
        hikariConfig.setUsername(postgres.getUsername());
        hikariConfig.setPassword(postgres.getPassword());
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        this.worker = new PsqlWorker(ds, true);
    }

    @Test
    public void getPersonsTest() {
        assertEquals(worker.getPersons().size(), 0, "List should be empty by now");
        Person p = new Person(Integer.MIN_VALUE, "bob", "dilenger");
        worker.insertPerson(p);
        assertEquals(worker.getPersons().size(), 1, "List should not be empty by now");

    }

    @Test
    public void anotherTest() {
        assertEquals(worker.getPersons().size(), 0, "List should be empty by now");

    }
}
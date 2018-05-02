package com.w11k.psql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComposeTest {
    @Rule
    DockerComposeContainer env = new DockerComposeContainer(new File("src/test/resources/compose-test.yml"))
            .withExposedService("db1", 1234, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(30)))
            .withExposedService("db2", 2345, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(30)));

    PsqlWorker testee1;
    PsqlWorker testee2;

    @BeforeEach
    public void setup() {

        env.starting(null);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:1234/postgres");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("test");
        HikariDataSource ds = new HikariDataSource(hikariConfig);

        // migrate schema with flyway
        Flyway flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();
        flyway.migrate();

        // init testee
        this.testee1 = new PsqlWorker(ds, false);


        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:2345/postgres");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("test");
        ds = new HikariDataSource(hikariConfig);

        // migrate schema with flyway
        flyway = new Flyway();
        flyway.setDataSource(ds);
        flyway.clean();
        flyway.migrate();

        this.testee2 = new PsqlWorker(ds, false);
    }

    @Test
    public void getPersonsTest() {
        assertEquals(testee1.getPersons().size(), 0, "List should be empty by now");
        Person p = new Person(Integer.MIN_VALUE, "bob", "dilenger");
        testee1.insertPerson(p);
        assertEquals(testee1.getPersons().size(), 1, "List should not be empty by now");

        assertEquals(testee2.getPersons().size(), 0, "List should be empty by now");
        testee2.insertPerson(p);
        assertEquals(testee2.getPersons().size(), 1, "List should not be empty by now");

    }
}

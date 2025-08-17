package org.tishfy.springcatalog.tests.controller;

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class BaseAutoTestConfiguration {

    static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("docker-compose.yml"))
                    .withExposedService("postgres", 5432)
                    .withExposedService("keycloak", 8080);

    static {
        environment.start();
    }
}

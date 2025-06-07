package pl.wiktor.devaudit.api.controller;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

// src/test/java/your/package/config/ContainersConfig.java

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:25.0";
  static final String REALM_IMPORT_FILE = "/realm-export.json";

  @Container
  public static final KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
          .withRealmImportFile(REALM_IMPORT_FILE);


  @Bean
  public KeycloakContainer keycloakContainer() {
    return keycloak;
  }
}

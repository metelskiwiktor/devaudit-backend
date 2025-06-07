package pl.wiktor.devaudit.util;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:25.0";
  static final String REALM_IMPORT_FILE = "/realm-export.json";

  @Container
  public static final KeycloakContainer keycloak = new KeycloakContainer(KEYCLOAK_IMAGE)
          .withRealmImportFile(REALM_IMPORT_FILE);

  static {
    keycloak.start();
  }

  @Bean
  public KeycloakContainer keycloakContainer() {
    return keycloak;
  }
}

package pl.wiktor.devaudit.api.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.wiktor.devaudit.api.controller.ContainersConfig;

@TestConfiguration(proxyBeanMethods = false)
public class KeycloakTestConfig {

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> ContainersConfig.keycloak.getAuthServerUrl() + "/realms/devaudit");
        registry.add("keycloak.server-url", ContainersConfig.keycloak::getAuthServerUrl);
    }

    @Bean
    public KeycloakTokenProvider keycloakTokenProvider(KeycloakContainer container) {
        return new KeycloakTokenProvider(container);
    }

    public static class KeycloakTokenProvider {
        private final KeycloakContainer container;

        public KeycloakTokenProvider(KeycloakContainer container) {
            this.container = container;
        }

        public String obtainToken(String username, String password) {
            Keycloak kc = KeycloakBuilder.builder()
                    .serverUrl(container.getAuthServerUrl())
                    .realm("devaudit")
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId("springboot-client")
                    .username(username)
                    .password(password)
                    .build();
            return kc.tokenManager().getAccessTokenString();
        }
    }
}

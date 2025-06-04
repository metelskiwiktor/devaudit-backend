package pl.wiktor.devaudit.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.wiktor.devaudit.api.response.SyncUsersResponse;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakClient;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakUserDTO;
import pl.wiktor.devaudit.domain.user.UserRole;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({ContainersConfig.class, AdminControllerIntegrationTest.TestConfig.class})
class AdminControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public KeycloakClient keycloakClient() {
            KeycloakClient mockKeycloakClient = Mockito.mock(KeycloakClient.class);

            // Mock the getAllUsers method to return a list of test users
            List<KeycloakUserDTO> mockUsers = List.of(
                new KeycloakUserDTO("user1", "user1@example.com", "User One", Set.of(UserRole.STUDENT)),
                new KeycloakUserDTO("user2", "user2@example.com", "User Two", Set.of(UserRole.MENTOR)),
                new KeycloakUserDTO("user3", "user3@example.com", "User Three", Set.of(UserRole.ADMIN, UserRole.MENTOR))
            );

            when(mockKeycloakClient.getAllUsers()).thenReturn(mockUsers);
            return mockKeycloakClient;
        }
    }

    @Autowired
    KeycloakContainer keycloak;

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> ContainersConfig.keycloak.getAuthServerUrl() + "/realms/devaudit");
    }

    private String obtainToken(String username, String password) {
        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("devaudit")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("springboot-client")
                .username(username)
                .password(password)
                .build();
        return kc.tokenManager().getAccessTokenString();
    }

    @Test
    void syncUsersShouldReturnSyncedUsersCount() {
        String token = obtainToken("admin", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SyncUsersResponse> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/admin/sync-users",
                HttpMethod.GET,
                entity,
                SyncUsersResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().syncedCount()).isEqualTo(3);
    }
}

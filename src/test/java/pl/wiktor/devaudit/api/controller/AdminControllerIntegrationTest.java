package pl.wiktor.devaudit.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import pl.wiktor.devaudit.api.response.SyncUsersResponse;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakClient;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakUserDTO;
import pl.wiktor.devaudit.domain.user.UserRole;


import java.util.List;
import java.util.Set;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(AdminControllerIntegrationTest.TestConfig.class)
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
    WebTestClient webTestClient;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void initClient() {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }


    @Test
    void syncUsersShouldReturnSyncedUsersCount() {
        webTestClient.mutateWith(SecurityMockServerConfigurers.mockJwt()
                        .jwt(jwt -> jwt.subject("admin")
                                .claim("realm_access", Map.of("roles", List.of("admin")))))
                .get()
                .uri("/api/admin/sync-users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SyncUsersResponse.class)
                .value(body -> assertThat(body.syncedCount()).isEqualTo(3));
    }
}

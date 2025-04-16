package pl.wiktor.devaudit.infrastructure.keycloak;

import pl.wiktor.devaudit.domain.UserRole;

public record KeycloakUserDTO(String id, String email, UserRole role) {
}
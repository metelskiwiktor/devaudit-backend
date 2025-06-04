package pl.wiktor.devaudit.infrastructure.keycloak;

import pl.wiktor.devaudit.domain.user.UserRole;

import java.util.Set;

public record KeycloakUserDTO(String id, String firstname, String email, Set<UserRole> roles) {
    public boolean isStudent() {
        return roles.contains(UserRole.STUDENT);
    }

    public boolean isMentor() {
        return roles.contains(UserRole.MENTOR);
    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }
}
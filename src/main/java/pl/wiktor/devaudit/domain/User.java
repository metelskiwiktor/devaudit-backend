package pl.wiktor.devaudit.domain;

public record User(String keycloakId, String email, UserRole role) {
}

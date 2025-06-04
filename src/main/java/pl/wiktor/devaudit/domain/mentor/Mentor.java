package pl.wiktor.devaudit.domain.mentor;

public record Mentor(String keycloakId, String firstname, String email, boolean isAdmin) {
}
package pl.wiktor.devaudit.domain.mentor;

public record Mentor(String keycloakId, String firstname, String email, boolean isAdmin) {
    public Mentor(String keycloakId, String firstname, String email) {
        this(keycloakId, firstname, email, false);
    }
}
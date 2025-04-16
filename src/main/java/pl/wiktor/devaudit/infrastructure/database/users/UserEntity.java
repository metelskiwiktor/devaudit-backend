package pl.wiktor.devaudit.infrastructure.database.users;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import pl.wiktor.devaudit.domain.UserRole;

@Entity
public class UserEntity {
    @Id
    private String keycloakId;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity() {
    }

    public UserEntity(String keycloakId, String email, UserRole role) {
        this.keycloakId = keycloakId;
        this.email = email;
        this.role = role;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

package pl.wiktor.devaudit.infrastructure.database.mentor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mentors")
public class MentorEntity {
    @Id
    private String keycloakId;
    private String firstname;
    private String email;
    private boolean admin;

    public MentorEntity() {
    }

    public MentorEntity(String keycloakId, String firstname, String email, boolean admin) {
        this.keycloakId = keycloakId;
        this.firstname = firstname;
        this.email = email;
        this.admin = admin;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
package pl.wiktor.devaudit.infrastructure.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.UUID;

@Component
public class KeycloakRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakRegistrationService.class);

    @Value("${keycloak.server-url}")
    private String serverUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.client-id}")
    private String clientId;
    
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    
    @Value("${keycloak.admin-username}")
    private String adminUsername;
    
    @Value("${keycloak.admin-password}")
    private String adminPassword;

    public String registerStudent(String firstName, String lastName, String email) {
        LOGGER.info("Registering new student in Keycloak: {}", email);
        
        try {
            Keycloak keycloak = getKeycloakClient();
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            // Generate a random password for the new user
            String password = UUID.randomUUID().toString();
            
            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(true);
            
            // Create credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(true);
            user.setCredentials(Collections.singletonList(credential));
            
            // Create user
            Response response = usersResource.create(user);
            String userId = getCreatedId(response);
            
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user in Keycloak");
            }
            
            // Assign role
            RoleRepresentation roleRepresentation = realmResource.roles().get("student").toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
            
            LOGGER.info("Student successfully registered in Keycloak with ID: {}", userId);
            return userId;
        } catch (Exception e) {
            LOGGER.error("Error registering student in Keycloak", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to register student in Keycloak", e);
        }
    }
    
    private String getCreatedId(Response response) {
        if (response.getStatus() == 201) {
            String location = response.getHeaderString("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }
        return null;
    }
    
    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}
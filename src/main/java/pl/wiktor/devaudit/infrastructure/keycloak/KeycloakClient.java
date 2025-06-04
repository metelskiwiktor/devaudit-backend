package pl.wiktor.devaudit.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.wiktor.devaudit.domain.user.UserRole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class KeycloakClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClient.class);

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

    public List<KeycloakUserDTO> getAllUsers() {
        LOGGER.info("Getting all users from Keycloak");
        Keycloak keycloak = getKeycloakClient();
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> keycloakUsers = usersResource.list();

        List<KeycloakUserDTO> result = keycloakUsers.stream()
                .map(user -> {
                    String userId = user.getId();
                    List<String> roleNames = getUserRoles(realmResource, userId);
                    Set<UserRole> roles = mapToUserRoles(roleNames);

                    return new KeycloakUserDTO(
                            userId,
                            user.getEmail(),
                            user.getFirstName(),
                            roles
                    );
                })
                .filter(dto -> !dto.roles().isEmpty())
                .collect(Collectors.toList());

        LOGGER.info("Retrieved {} valid users from Keycloak", result.size());
        return result;
    }

    private Set<UserRole> mapToUserRoles(List<String> roleNames) {
        Set<UserRole> roles = new HashSet<>();

        for (String roleName : roleNames) {
            try {
                roles.add(UserRole.valueOf(roleName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Unknown role: {}", roleName);
            }
        }

        return roles;
    }
    
    private List<String> getUserRoles(RealmResource realmResource, String userId) {
        List<RoleRepresentation> roleRepresentations = realmResource.users().get(userId)
                .roles().realmLevel().listAll();
        
        return roleRepresentations.stream()
                .map(RoleRepresentation::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
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

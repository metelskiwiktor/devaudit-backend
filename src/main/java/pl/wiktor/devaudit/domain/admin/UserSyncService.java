package pl.wiktor.devaudit.domain.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.wiktor.devaudit.domain.User;
import pl.wiktor.devaudit.domain.UserRepository;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakClient;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakUserDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncService.class);

    private final UserRepository userRepository;
    private final KeycloakClient keycloakClient;

    public UserSyncService(UserRepository userRepository, KeycloakClient keycloakClient) {
        this.userRepository = userRepository;
        this.keycloakClient = keycloakClient;
    }

    public int syncUsers() {
        LOGGER.info("Starting user synchronization");
        List<KeycloakUserDTO> keycloakUsers = keycloakClient.getAllUsers();

        int syncedCount = 0;

        for (KeycloakUserDTO keycloakUser : keycloakUsers) {
            try {
                String userId = keycloakUser.id();
                String email = keycloakUser.email();

                Optional<User> existingUser = userRepository.findById(userId);

                if (existingUser.isPresent()) {
                    User user = existingUser.get();
                    boolean needsUpdate = !user.email().equals(email) || user.role() != keycloakUser.role();

                    if (needsUpdate) {
                        LOGGER.info("Updating user: {}", userId);
                        User updatedUser = new User(userId, email, keycloakUser.role());
                        userRepository.save(updatedUser);
                        syncedCount++;
                    }
                } else {
                    LOGGER.info("Adding new user: {}", userId);
                    User newUser = new User(userId, email, keycloakUser.role());
                    userRepository.save(newUser);
                    syncedCount++;
                }
            } catch (Exception e) {
                LOGGER.error("Error syncing user: {}", keycloakUser.id(), e);
            }
        }

        LOGGER.info("User synchronization completed. Synced {} users", syncedCount);
        return syncedCount;
    }
}

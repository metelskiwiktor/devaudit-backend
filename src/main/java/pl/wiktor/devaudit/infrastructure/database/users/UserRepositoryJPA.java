package pl.wiktor.devaudit.infrastructure.database.users;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.User;
import pl.wiktor.devaudit.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Repository
public class UserRepositoryJPA implements UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryJPA.class);

    private final UserRepositorySpring userRepository;
    private final ConversionService conversionService;

    public UserRepositoryJPA(UserRepositorySpring userRepository, ConversionService conversionService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public void save(User user) {
        LOGGER.debug("Saving user with id: {}", user.keycloakId());
        UserEntity userEntity = conversionService.convert(user, UserEntity.class);
        if (userEntity != null) {
            userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("UserEntity is null after conversion, user data: " + user);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        LOGGER.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .map(entity -> new User(entity.getKeycloakId(), entity.getEmail(), entity.getRole()));
    }
}

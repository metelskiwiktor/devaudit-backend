package pl.wiktor.devaudit.infrastructure.database.users;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;
import pl.wiktor.devaudit.domain.User;
import pl.wiktor.devaudit.domain.UserRepository;

@Repository
public class UserRepositoryJPA implements UserRepository {
    private final UserRepositorySpring userRepository;
    private final ConversionService conversionService;

    public UserRepositoryJPA(UserRepositorySpring userRepository, ConversionService conversionService) {
        this.userRepository = userRepository;
        this.conversionService = conversionService;
    }

    @Override
    public void save(User user) {
        UserEntity userEntity = conversionService.convert(user, UserEntity.class);
        if (userEntity != null) {
            userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("UserEntity is null after conversion, user data: " + user);
        }
    }
}

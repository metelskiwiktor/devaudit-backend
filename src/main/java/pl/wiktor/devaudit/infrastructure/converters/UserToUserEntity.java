package pl.wiktor.devaudit.infrastructure.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.wiktor.devaudit.domain.User;
import pl.wiktor.devaudit.infrastructure.database.users.UserEntity;

@Component
public class UserToUserEntity implements Converter<User, UserEntity> {
    @Override
    public UserEntity convert(User user) {
        return new UserEntity(user.keycloakId(), user.email(), user.role());
    }
}

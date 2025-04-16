package pl.wiktor.devaudit.domain;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
}
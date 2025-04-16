package pl.wiktor.devaudit.infrastructure.database.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositorySpring extends JpaRepository<UserEntity, String> {
}

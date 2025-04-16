package pl.wiktor.devaudit.domain.exception;

import org.springframework.http.HttpStatus;
import pl.wiktor.devaudit.domain.user.UserRole;

public class InvalidUserRoleException extends DomainException {
    private static final String MESSAGE_TEMPLATE = "User with id '%s' should has role '%s', but has '%s'";

    public InvalidUserRoleException(String id, UserRole shouldHasRole, UserRole hasRole, HttpStatus status) {
        super(MESSAGE_TEMPLATE.formatted(id, shouldHasRole, hasRole), status);
    }
}

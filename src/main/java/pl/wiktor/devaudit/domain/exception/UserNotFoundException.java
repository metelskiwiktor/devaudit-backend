package pl.wiktor.devaudit.domain.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DomainException {
    private final static String MESSAGE_TEMPLATE = "User with id '%s' not found";

    public UserNotFoundException(String id, HttpStatus status) {
        super(MESSAGE_TEMPLATE.formatted(id), status);
    }
}

package pl.wiktor.devaudit.domain.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public DomainException(String message, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}

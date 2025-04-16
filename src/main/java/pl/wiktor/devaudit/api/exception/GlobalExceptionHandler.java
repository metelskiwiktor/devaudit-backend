package pl.wiktor.devaudit.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.wiktor.devaudit.domain.exception.DomainException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
        return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getMessage(), e.getStatus().value()));
    }
}

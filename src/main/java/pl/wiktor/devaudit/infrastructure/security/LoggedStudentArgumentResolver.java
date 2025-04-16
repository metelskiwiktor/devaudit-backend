package pl.wiktor.devaudit.infrastructure.security;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pl.wiktor.devaudit.domain.exception.UserNotFoundException;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.domain.student.StudentRepository;

@Component
public class LoggedStudentArgumentResolver implements HandlerMethodArgumentResolver {
    private final StudentRepository studentRepository;

    public LoggedStudentArgumentResolver(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedStudent.class) &&
               parameter.getParameterType().equals(Student.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = (Authentication) webRequest.getUserPrincipal();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        String keycloakId = jwt.getSubject();

        return studentRepository.findById(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId, HttpStatus.UNAUTHORIZED));
    }
}
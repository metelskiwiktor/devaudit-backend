package pl.wiktor.devaudit.infrastructure.security.annotation;

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
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;

@Component
public class LoggedMentorArgumentResolver implements HandlerMethodArgumentResolver {
    private final MentorRepository mentorRepository;

    public LoggedMentorArgumentResolver(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedMentor.class) &&
                parameter.getParameterType().equals(Mentor.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        Authentication authentication = (Authentication) webRequest.getUserPrincipal();
        if (!(authentication != null && authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        String keycloakId = jwt.getSubject();

        return mentorRepository.findById(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(keycloakId, HttpStatus.UNAUTHORIZED));

    }
}
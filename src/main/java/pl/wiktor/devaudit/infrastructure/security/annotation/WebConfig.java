package pl.wiktor.devaudit.infrastructure.security.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoggedMentorArgumentResolver mentorArgumentResolver;
    private final LoggedStudentArgumentResolver studentArgumentResolver;

    public WebConfig(@Lazy LoggedMentorArgumentResolver mentorArgumentResolver, @Lazy LoggedStudentArgumentResolver studentArgumentResolver) {
        this.mentorArgumentResolver = mentorArgumentResolver;
        this.studentArgumentResolver = studentArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(mentorArgumentResolver);
        resolvers.add(studentArgumentResolver);
    }
}
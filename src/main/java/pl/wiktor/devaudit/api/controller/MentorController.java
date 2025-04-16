package pl.wiktor.devaudit.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.infrastructure.security.LoggedMentor;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    @GetMapping("/example")
    public void example(@LoggedMentor Mentor user) {
        System.out.println("User: " + user);
    }
}

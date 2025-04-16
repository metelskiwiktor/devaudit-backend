package pl.wiktor.devaudit.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wiktor.devaudit.domain.student.Student;
import pl.wiktor.devaudit.infrastructure.security.LoggedStudent;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @GetMapping("/example")
    public void example(@LoggedStudent Student student) {
        System.out.println("student: " + student);
    }
}

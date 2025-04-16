package pl.wiktor.devaudit.api.controller.mentor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {
    @GetMapping("/example")
    public String adminEndpoint() {
        return "Accessible by mentor only";
    }
}

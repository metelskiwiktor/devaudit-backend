package pl.wiktor.devaudit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyRepository;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import pl.wiktor.devaudit.DevauditBackendApplication;

@SpringBootTest(classes = DevauditBackendApplication.class)
@AutoConfigureMockMvc
class SurveyMentorControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static final String MENTOR_ID = "mentor-1";

    @BeforeEach
    void setup() {
        mentorRepository.save(new Mentor(MENTOR_ID, "John", "john@example.com", false));
    }

    @Test
    void generateSurveyStoresStudentInfo() throws Exception {
        String body = objectMapper.writeValueAsString(
                new GenerateSurveyRequest("Alice", "Smith", "alice@example.com")
        );

        var result = mockMvc.perform(
                post("/api/mentor/survey/generate")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(MENTOR_ID))
                                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_MENTOR"))
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        Survey survey = surveyRepository.findById(id).orElseThrow();
        assertThat(survey.status()).isEqualTo(SurveyStatus.PENDING);
        assertThat(survey.studentInfo().firstName()).isEqualTo("Alice");
        assertThat(survey.completedDate()).isNull();
    }

    private record GenerateSurveyRequest(String firstName, String lastName, String email) {}
}

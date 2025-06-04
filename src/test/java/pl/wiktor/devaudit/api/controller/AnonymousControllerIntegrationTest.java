package pl.wiktor.devaudit.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.wiktor.devaudit.domain.mentor.Mentor;
import pl.wiktor.devaudit.domain.mentor.MentorRepository;
import pl.wiktor.devaudit.domain.student.StudentRepository;
import pl.wiktor.devaudit.domain.survey.Survey;
import pl.wiktor.devaudit.domain.survey.SurveyFormRepository;
import pl.wiktor.devaudit.domain.survey.SurveyRepository;
import pl.wiktor.devaudit.domain.survey.SurveyStatus;
import pl.wiktor.devaudit.domain.survey.SurveyStudentInfo;
import pl.wiktor.devaudit.api.request.SubmitSurveyFormRequest;
import pl.wiktor.devaudit.infrastructure.keycloak.KeycloakRegistrationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import pl.wiktor.devaudit.DevauditBackendApplication;

@SpringBootTest(classes = DevauditBackendApplication.class)
@AutoConfigureMockMvc
class AnonymousControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    SurveyFormRepository surveyFormRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    KeycloakRegistrationService keycloakRegistrationService;

    private static final String MENTOR_ID = "mentor-2";

    @BeforeEach
    void setup() {
        mentorRepository.save(new Mentor(MENTOR_ID, "Jane", "jane@example.com", false));
        Mockito.when(keycloakRegistrationService.registerStudent(anyString(), anyString(), anyString())).thenReturn("student-1");
    }

    @Test
    void submitSurveyMarksCompleted() throws Exception {
        Survey survey = Survey.create(new Mentor(MENTOR_ID, "Jane", "jane@example.com", false), new SurveyStudentInfo("Bob", "Brown", "bob@example.com"));
        surveyRepository.save(survey);

        SubmitSurveyFormRequest request = createRequest();
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/anonymous/survey/" + survey.id() + "/submit")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        Survey updated = surveyRepository.findById(survey.id()).orElseThrow();
        assertThat(updated.status()).isEqualTo(SurveyStatus.COMPLETED);
        assertThat(surveyFormRepository.findBySurveyId(survey.id())).isPresent();
        assertThat(studentRepository.findAll()).hasSize(1);
    }

    private SubmitSurveyFormRequest createRequest() {
        return new SubmitSurveyFormRequest(
                new SubmitSurveyFormRequest.PersonalInfo("Bob", "Brown", "bob@example.com", "", "", "", "", "", 25, "", false),
                new SubmitSurveyFormRequest.ProgrammingExperience("", "", "", java.util.Map.of(), "", "", true, "", ""),
                new SubmitSurveyFormRequest.LearningGoals("", "", "", java.util.List.of(), false, false, "", ""),
                new SubmitSurveyFormRequest.LearningPreferences(java.util.List.of(), java.util.Map.of(), "", java.util.List.of(), "", 1, 60, "", false),
                new SubmitSurveyFormRequest.AdditionalInfo("", "", "", "", "", "", false, "")
        );
    }
}

package pl.wiktor.devaudit.infrastructure.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.wiktor.devaudit.api.request.SubmitSurveyFormRequest;
import pl.wiktor.devaudit.domain.survey.SurveySubmission;

@Component
public class SubmitSurveyFormRequestToSurveySubmission implements Converter<SubmitSurveyFormRequest, SurveySubmission> {

    @Override
    public SurveySubmission convert(SubmitSurveyFormRequest submitSurveyFormRequest) {
        return new SurveySubmission(
                mapPersonalInfo(submitSurveyFormRequest.personalInfo()),
                mapProgrammingExperience(submitSurveyFormRequest.programmingExperience()),
                mapLearningGoals(submitSurveyFormRequest.learningGoals()),
                mapLearningPreferences(submitSurveyFormRequest.learningPreferences()),
                mapAdditionalInfo(submitSurveyFormRequest.additionalInfo())
        );
    }

    private SurveySubmission.PersonalInfo mapPersonalInfo(SubmitSurveyFormRequest.PersonalInfo info) {
        return new SurveySubmission.PersonalInfo(
                info.fullName(),
                info.email(),
                info.phone(),
                info.education(),
                info.githubUrl(),
                info.linkedinUrl(),
                info.currentOccupation(),
                info.age(),
                info.city(),
                info.remoteOnly()
        );
    }

    private SurveySubmission.ProgrammingExperience mapProgrammingExperience(SubmitSurveyFormRequest.ProgrammingExperience exp) {
        return new SurveySubmission.ProgrammingExperience(
                exp.javaExperience(),
                exp.otherLanguages(),
                exp.priorProjects(),
                exp.skillLevels(),
                exp.previousCourses(),
                exp.preferredIDE(),
                exp.hasUsedGit(),
                exp.databaseExperience(),
                exp.buildToolsExperience()
        );
    }

    private SurveySubmission.LearningGoals mapLearningGoals(SubmitSurveyFormRequest.LearningGoals goals) {
        return new SurveySubmission.LearningGoals(
                goals.mainGoal(),
                goals.timeline(),
                goals.expectedOutcome(),
                goals.specificTopics(),
                goals.wantsIndustryProject(),
                goals.needsCertification(),
                goals.careerPlans(),
                goals.motivationLevel()
        );
    }

    private SurveySubmission.LearningPreferences mapLearningPreferences(SubmitSurveyFormRequest.LearningPreferences prefs) {
        return new SurveySubmission.LearningPreferences(
                prefs.learningStyle(),
                prefs.availabilityByDay(),
                prefs.availabilityNotes(),
                prefs.materialTypes(),
                prefs.preferredCommunication(),
                prefs.sessionsPerWeek(),
                prefs.preferredSessionLength(),
                prefs.feedbackFrequency(),
                prefs.needsFlexibleSchedule()
        );
    }

    private SurveySubmission.AdditionalInfo mapAdditionalInfo(SubmitSurveyFormRequest.AdditionalInfo info) {
        return new SurveySubmission.AdditionalInfo(
                info.availableTools(),
                info.difficulties(),
                info.additionalNotes(),
                info.howDidYouFindUs(),
                info.computerSpecs(),
                info.internetSpeed(),
                info.specialAccommodations(),
                info.accommodationDetails()
        );
    }
}

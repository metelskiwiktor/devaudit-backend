package pl.wiktor.devaudit.infrastructure.database.survey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "survey_forms")
public class SurveyFormEntity {
    
    @Id
    private String id;
    
    private String surveyId;
    
    @Lob
    @Column(columnDefinition = "CLOB")
    private String formData;
    
    private LocalDateTime submissionDate;

    public SurveyFormEntity() {
        this.id = UUID.randomUUID().toString();
        this.submissionDate = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
}
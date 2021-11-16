package no.kristiania.exam.survey;

public class Question {

    private String name;
    private Long id;
    private Long surveyId;
    private int numberOfAnswers;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public int getNumberOfAnswers() {
        return numberOfAnswers;
    }

    public void setNumberOfAnswers(int numberOfAnswers) {
        this.numberOfAnswers = numberOfAnswers;
    }

    @Override
    public String toString() {
        return "Question{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", surveyId=" + surveyId +
                ", numberOfAnswers=" + numberOfAnswers +
                '}';
    }
}

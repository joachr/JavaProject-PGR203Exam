package no.kristiania.exam.survey;

public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String eMail;
    private Long surveyDone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public Long getSurveyDone() {
        return surveyDone;
    }

    public void setSurveyDone(Long surveyDone) {
        this.surveyDone = surveyDone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", eMail='" + eMail + '\'' +
                ", surveyDone=" + surveyDone +
                '}';
    }
}

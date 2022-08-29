package no.kristiania.exam.survey;

public class Survey {

    private String name;
    private Long id;
    private int numberOfQuestions;

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

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", numberOfQuestions=" + numberOfQuestions +
                '}';
    }
}

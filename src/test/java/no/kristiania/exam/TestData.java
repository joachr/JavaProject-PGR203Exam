package no.kristiania.exam;

import no.kristiania.exam.survey.Answer;
import no.kristiania.exam.survey.Question;
import no.kristiania.exam.survey.Survey;
import no.kristiania.exam.survey.User;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import javax.sql.DataSource;
import java.util.Random;

public class TestData {

    public static DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:exam_survey_db;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        return dataSource;
    }

    public static Random random = new Random();

    public static Survey exampleSurvey() {
        Survey survey = new Survey();
        survey.setName(pickOne("Alcohol survey", "Car survey", "Vacation survey", "E-sport survey"));
        survey.setId(1L);

        return survey;
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setName(pickOne("Who are you?", "What is the purpose?", "Am I alone here?", "Is this a simulator?"));
        question.setSurveyId(pickOneLong(1L));
        return question;
    }

    public static Answer exampleAnswer() {
        Answer answer = new Answer();
        answer.setName(pickOne("Correct", "So wrong", "Bad", "Good"));

        return answer;
    }

    public static User exampleUser() {
        User user = new User();
        user.setFirstName(pickOne("Ben", "Spiderman", "The Rock", "Batman"));
        user.setLastName(pickOne("Affleck", "Parker", "Johnson", "Wayne"));
        user.seteMail(pickOne("affleck@affleck.com", "maryjaneloverboy@yahoo.com", "bigboi96@gmail.com", "jokerkilla@hotmail.com"));
        user.setSurveyDone(exampleSurvey().getId());

        return user;
    }

    public static String pickOne(String... alternates) {
        return alternates[random.nextInt(alternates.length)];
    }

    public static Long pickOneLong(Long... alternates) {
        return alternates[random.nextInt(alternates.length)];
    }

}

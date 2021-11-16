package no.kristiania.exam.survey;

import no.kristiania.exam.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;


public class QuestionDaoTest {

    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());

    @BeforeAll
    static void initSurvey() throws SQLException {
        SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
        Survey survey1 = new Survey();
        survey1.setName("Fiskeundersøkelse");
        surveyDao.saveSurvey(survey1);
        Survey survey2 = new Survey();
        survey2.setName("Hesteundersøkelse");
        surveyDao.saveSurvey(survey2);
    }

    @Test
    void shouldRetrieveSavedQuestionFromDatabase() throws SQLException, UnsupportedEncodingException {
        Question question = TestData.exampleQuestion();
        question.setSurveyId(0L);
        questionDao.saveQuestion(question);

        assertThat(questionDao.retrieveQuestion(question.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldListQuestionsBySurveyId() throws SQLException, UnsupportedEncodingException {
        Question matchingQuestion = TestData.exampleQuestion();
        questionDao.saveQuestion(matchingQuestion);
        questionDao.setSurveyIdByQuestionId(matchingQuestion.getId(), 1L);

        Question anotherMatchingQuestion = TestData.exampleQuestion();
        questionDao.saveQuestion(anotherMatchingQuestion);
        questionDao.setSurveyIdByQuestionId(anotherMatchingQuestion.getId(), 1L);

        Question nonMatchingQuestion = TestData.exampleQuestion();
        questionDao.saveQuestion(nonMatchingQuestion);
        questionDao.setSurveyIdByQuestionId(nonMatchingQuestion.getId(), 2L);

        assertThat(questionDao.listQuestionsBySurveyId(matchingQuestion.getSurveyId()))
                .extracting(Question::getId)
                .contains(matchingQuestion.getId(), anotherMatchingQuestion.getId())
                .doesNotContain(nonMatchingQuestion.getId());
    }

    @Test
    void shouldDeleteQuestion() throws SQLException, UnsupportedEncodingException {
        Question questionForSaving = TestData.exampleQuestion();
        questionForSaving.setSurveyId(1L);
        questionDao.saveQuestion(questionForSaving);

        Question anotherQuestionForSaving = TestData.exampleQuestion();
        anotherQuestionForSaving.setSurveyId(questionForSaving.getSurveyId());
        questionDao.saveQuestion(anotherQuestionForSaving);

        Question questionForDeleting = TestData.exampleQuestion();
        questionForDeleting.setSurveyId(2L);
        questionDao.saveQuestion(questionForDeleting);

        System.out.println(questionDao.listAll());

        questionDao.deleteQuestion(questionForDeleting.getId());

        System.out.println(questionDao.listAll());

        assertThat(questionDao.listAll())
                .extracting(Question::getId)
                .contains(questionForSaving.getId(), anotherQuestionForSaving.getId())
                .doesNotContain(questionForDeleting.getId());
    }

    @Test
    void shouldSetSurveyIdToNull() throws SQLException, UnsupportedEncodingException {
        Question questionForSaving = TestData.exampleQuestion();
        questionForSaving.setSurveyId(2L);
        questionDao.saveQuestion(questionForSaving);

        Question anotherQuestionForSaving = TestData.exampleQuestion();
        anotherQuestionForSaving.setSurveyId(questionForSaving.getSurveyId());
        questionDao.saveQuestion(anotherQuestionForSaving);

        questionDao.setSurveyIdToNull(questionForSaving.getSurveyId());

        assertThat(questionDao.listQuestionsBySurveyId(questionForSaving.getSurveyId()))
                .extracting(Question::getId)
                .doesNotContain(questionForSaving.getId(), anotherQuestionForSaving.getId());
    }
}

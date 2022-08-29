package no.kristiania.exam.survey;

import no.kristiania.exam.TestData;
import org.junit.jupiter.api.Test;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class SurveyDaoTest {

    private final SurveyDao surveyDao = new SurveyDao(TestData.testDataSource());
    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());


    @Test
    void shouldRetrieveSavedSurveyFromDatabase() throws SQLException {
        Survey survey = TestData.exampleSurvey();
        surveyDao.saveSurvey(survey);

        assertThat(surveyDao.retrieveSurvey(survey.getId()))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(survey);
    }


    @Test
    void shouldListSavedSurveysByName() throws SQLException, UnsupportedEncodingException {
        Survey matchingSurvey = TestData.exampleSurvey();
        matchingSurvey.setName("Undersøkelse om fisk");
        surveyDao.saveSurvey(matchingSurvey);

        Survey anotherMatchingSurvey = TestData.exampleSurvey();
        anotherMatchingSurvey.setName(matchingSurvey.getName());
        surveyDao.saveSurvey(anotherMatchingSurvey);

        Survey nonMatchingSurvey = TestData.exampleSurvey();
        surveyDao.saveSurvey(nonMatchingSurvey);

        System.out.println(surveyDao.listBySurveyName(matchingSurvey.getName()));

        assertThat(surveyDao.listBySurveyName(matchingSurvey.getName()))
                .extracting(Survey::getId)
                .contains(matchingSurvey.getId(), anotherMatchingSurvey.getId())
                .doesNotContain(nonMatchingSurvey.getId());
    }

    @Test
    void shouldCountQuestionsInSurvey() throws SQLException, UnsupportedEncodingException {
        Survey survey = TestData.exampleSurvey();
        survey.setId(2L);
        surveyDao.saveSurvey(survey);

        Question question1 = TestData.exampleQuestion();
        questionDao.saveQuestion(question1);
        questionDao.setSurveyIdByQuestionId(questionDao.retrieveQuestion(question1.getId()).getId(),
                surveyDao.retrieveSurvey(survey.getId()).getId());

        Question question2 = TestData.exampleQuestion();
        questionDao.saveQuestion(question2);
        questionDao.setSurveyIdByQuestionId(questionDao.retrieveQuestion(question2.getId()).getId(),
                surveyDao.retrieveSurvey(survey.getId()).getId());

        Question question3 = TestData.exampleQuestion();
        questionDao.saveQuestion(question3);
        questionDao.setSurveyIdByQuestionId(questionDao.retrieveQuestion(question3.getId()).getId(),
                surveyDao.retrieveSurvey(survey.getId()).getId());

        assertThat(surveyDao.questionCounter(survey))
                .isEqualTo(survey.getNumberOfQuestions());
    }
}

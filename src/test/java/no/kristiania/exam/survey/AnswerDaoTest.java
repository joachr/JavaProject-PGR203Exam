package no.kristiania.exam.survey;

import no.kristiania.exam.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;

public class AnswerDaoTest {

    private final QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
    private final AnswerDao answerDao = new AnswerDao(TestData.testDataSource());

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
    void shouldListAnswersByQuestionId() throws SQLException {
        Question rightQuestion = TestData.exampleQuestion();
        questionDao.saveQuestion(rightQuestion);

        Question wrongQuestion = TestData.exampleQuestion();
        questionDao.saveQuestion(wrongQuestion);

        Answer answer1 = TestData.exampleAnswer();
        answer1.setQuestionId(rightQuestion.getId());
        answerDao.saveAnswer(answer1);

        Answer answer2 = TestData.exampleAnswer();
        answer2.setQuestionId(rightQuestion.getId());
        answerDao.saveAnswer(answer2);

        Answer answer3 = TestData.exampleAnswer();
        answer3.setQuestionId(wrongQuestion.getId());
        answerDao.saveAnswer(answer3);

        assertThat(answerDao.listAnswerByQuestionId(rightQuestion.getId()))
                .extracting(Answer::getId)
                .contains(answer1.getId(), answer2.getId())
                .doesNotContain(answer3.getId());
    }

    @Test
    void shouldCountAnswersInQuestion() throws SQLException {
        Question question = TestData.exampleQuestion();
        questionDao.saveQuestion(question);

        Answer answer1 = TestData.exampleAnswer();
        answer1.setQuestionId(question.getId());
        answerDao.saveAnswer(answer1);

        Answer answer2 = TestData.exampleAnswer();
        answer2.setQuestionId(question.getId());
        answerDao.saveAnswer(answer2);

        Answer answer3 = TestData.exampleAnswer();
        answer3.setQuestionId(question.getId());
        answerDao.saveAnswer(answer3);

        assertThat(questionDao.answerCounter(question))
                .isEqualTo(question.getNumberOfAnswers());
    }

    @Test
    void shouldDeleteAnswerByQuestionId() throws SQLException {
        Question questionWhereAnswersWillBeDeleted = TestData.exampleQuestion();
        questionDao.saveQuestion(questionWhereAnswersWillBeDeleted);

        Answer answer1 = TestData.exampleAnswer();
        answer1.setQuestionId(questionWhereAnswersWillBeDeleted.getId());
        answerDao.saveAnswer(answer1);

        Answer answer2 = TestData.exampleAnswer();
        answer2.setQuestionId(questionWhereAnswersWillBeDeleted.getId());
        answerDao.saveAnswer(answer2);

        assertThat(answerDao.listAnswerByQuestionId(questionWhereAnswersWillBeDeleted.getId()))
                .extracting(Answer::getId)
                .contains(answer1.getId(), answer2.getId());

        answerDao.deleteAnswersByQuestionId(questionWhereAnswersWillBeDeleted.getId());

        assertThat(answerDao.listAnswerByQuestionId(questionWhereAnswersWillBeDeleted.getId()))
                .extracting(Answer::getId)
                .doesNotContain(answer1.getId(), answer2.getId());
    }
}

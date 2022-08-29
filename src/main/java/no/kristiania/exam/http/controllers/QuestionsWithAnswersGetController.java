package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.survey.AnswerDao;
import no.kristiania.exam.survey.Question;
import no.kristiania.exam.survey.QuestionDao;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class QuestionsWithAnswersGetController implements HttpController {

    public static final String PATH = "/api/questionsAndAnswers";
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    public QuestionsWithAnswersGetController(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        String response = "";

        int index = 0;
        for (Question question : questionDao.listAll()) {
                    response += "<p>" + question.getName() + "</p>" +
                                "<select id='answers'" + index + " name='answers'>" +
                                answerDao.findAllAnswers(question.getId()) +
                                "</select><br><p>------------------------------------" + "</p>";
                    index++;
        }
        return new HttpMessage("HTTP/1.1 200 OK", response);
    }


}

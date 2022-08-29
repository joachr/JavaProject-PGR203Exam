package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.Answer;
import no.kristiania.exam.survey.AnswerDao;
import no.kristiania.exam.survey.QuestionDao;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class OptionPostController implements HttpController {

    public static final String PATH = "/api/addOptions";
    private final AnswerDao answerDao;
    private final QuestionDao questionDao;

    public OptionPostController(AnswerDao answerDao, QuestionDao questionDao) {
        this.answerDao = answerDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        QueryString parameters = new QueryString(request.getMessageBody());

        Answer answer = new Answer();
        answer.setName(parameters.getParameter("option"));
        String decodedNewName = java.net.URLDecoder.decode(answer.getName(), StandardCharsets.UTF_8.name());
        answer.setName(decodedNewName);
        answer.setQuestionId(questionDao.retrieveIdByName(parameters.getParameter("questions")));
        answerDao.saveAnswer(answer);

        questionDao.setNumOfAnswers(questionDao.answerCounter(questionDao.retrieveQuestion(answer.getQuestionId())), questionDao.retrieveQuestion(answer.getQuestionId()).getId());

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/addQuestion.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.Question;
import no.kristiania.exam.survey.QuestionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class QuestionPostController implements HttpController {
    private final QuestionDao questionDao;
    public static final String PATH = "/api/newQuestion";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public QuestionPostController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        QueryString parameters = new QueryString(request.getMessageBody());
        Question question = new Question();
        question.setName(parameters.getParameter("name"));
        String decodedQuestionName = java.net.URLDecoder.decode(question.getName(), StandardCharsets.UTF_8.name());
        question.setName(decodedQuestionName);

        questionDao.saveQuestion(question);
        logger.info("Question: " + "'" + question.getName() + "'" + " has been added to the database");

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/addQuestion.html");
        return httpMessage;
    }
}

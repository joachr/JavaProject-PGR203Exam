package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.AnswerDao;
import no.kristiania.exam.survey.QuestionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class QuestionDeleteController implements HttpController {
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    public static final String PATH = "/api/deleteQuestion";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public QuestionDeleteController(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        QueryString parameters = new QueryString(request.getMessageBody());

        String decodedQuestionName = java.net.URLDecoder.decode(parameters.getParameter("questions"), StandardCharsets.UTF_8.name());
        Long id = questionDao.retrieveIdByName(decodedQuestionName);

        answerDao.deleteAnswersByQuestionId(id);
        questionDao.deleteQuestion(id);
        logger.info("Question: " + "'" + decodedQuestionName + "'" + " has been deleted from the database");

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/addQuestion.html");
        return httpMessage;
    }
}

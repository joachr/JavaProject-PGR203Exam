package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.Question;
import no.kristiania.exam.survey.QuestionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class QuestionUpdateController implements HttpController {
    private final QuestionDao questionDao;
    public static final String PATH = "/api/updateQuestion";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public QuestionUpdateController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException, IOException {
        QueryString parameters = new QueryString(request.getMessageBody());

        String decodedQuestionName = URLDecoder.decode(parameters.getParameter("question_name"), StandardCharsets.UTF_8.name());
        String decodedQuestion = URLDecoder.decode(parameters.getParameter("question"), StandardCharsets.UTF_8.name());
        Long id = questionDao.retrieveIdByName(decodedQuestion);


        Question questionToUpdate = questionDao.retrieveById(id);
        questionToUpdate.setName(decodedQuestionName);
        questionDao.update(questionToUpdate, id);

        logger.info("Question: " + "'" + questionToUpdate.getName() + "'" + " has been updated in the database");

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/addQuestion.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.QuestionDao;
import no.kristiania.exam.survey.SurveyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class SurveyDeleteController implements HttpController {
    private final SurveyDao surveyDao;
    private final QuestionDao questionDao;
    public static final String PATH = "/api/deleteSurvey";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public SurveyDeleteController(SurveyDao surveyDao, QuestionDao questionDao) {
        this.surveyDao = surveyDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        QueryString parameters = new QueryString(request.getMessageBody());

        String decodedSurveyName = java.net.URLDecoder.decode(parameters.getParameter("survey"), StandardCharsets.UTF_8.name());
        Long id = surveyDao.retrieveSurveyIdByName(decodedSurveyName);

        questionDao.setSurveyIdToNull(id);
        surveyDao.deleteSurvey(id);
        logger.info("Survey: " + "'" + decodedSurveyName + "'" + " has been deleted from the database");


        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/addQuestion.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.survey.QuestionDao;
import no.kristiania.exam.survey.Survey;
import no.kristiania.exam.survey.SurveyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SurveyPostController implements HttpController {

    private final SurveyDao surveyDao;
    private final QuestionDao questionDao;
    public static final String PATH = "/api/newSurvey";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public SurveyPostController(SurveyDao surveyDao, QuestionDao questionDao) {
        this.surveyDao = surveyDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException, IOException {
        Map<String, String> parameters = HttpMessage.parseMultipleRequestParameters(request.getMessageBody());

        Survey survey = new Survey();
        survey.setName(parameters.get("surveyName"));
        String decodedNewName = java.net.URLDecoder.decode(survey.getName(), StandardCharsets.UTF_8.name()).replaceAll(" ", "");
        survey.setName(decodedNewName);
        surveyDao.saveSurvey(survey);
        logger.info("Survey: " + "'" + survey.getName() + "'" + " has been added to the database");

        int index = 1;
        ArrayList<Long> list = new ArrayList<>();
        while (parameters.size() > index) {
            list.add(questionDao.retrieveIdByName(URLDecoder.decode(parameters.get("questions" + index))));
            index++;
        }

        for (Long id : list) {
            questionDao.setSurveyIdByQuestionId(id, surveyDao.retrieveSurvey(survey.getId()).getId());
        }

        surveyDao.setNumOfQuestionsOnSurvey(surveyDao.questionCounter(surveyDao.retrieveSurvey(survey.getId())), surveyDao.retrieveSurvey(survey.getId()).getId());

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/newSurvey.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.survey.Survey;
import no.kristiania.exam.survey.SurveyDao;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class SurveyGetController implements HttpController {

    public static final String PATH = "/api/surveys";
    private final SurveyDao surveyDao;

    public SurveyGetController(SurveyDao surveyDao) {
        this.surveyDao = surveyDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        String response = "";
        for (Survey survey : surveyDao.listAll()) {
            response += "<option value=" + survey.getName() + ">" + survey.getName() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", response);
    }

}

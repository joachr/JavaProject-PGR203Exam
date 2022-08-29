package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class SurveyCompletePostController implements HttpController {

    private final SurveyDao surveyDao;
    private final UserDao userDao;
    public static final String PATH = "/api/completedSurvey";

    public SurveyCompletePostController(SurveyDao surveyDao, UserDao userDao) {
        this.surveyDao = surveyDao;
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {
        QueryString parameters = new QueryString(request.getMessageBody());

        Long surveyId = surveyDao.retrieveSurveyIdByName(URLDecoder.decode(parameters.getParameter("chosenSurvey")));
        Long userId = userDao.retrieveUserIdByName(URLDecoder.decode(parameters.getParameter("user")));

        userDao.setSurveyId(surveyId, userId);

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/takeSurvey.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class SurveyGetSpecificController implements HttpController {

    public static final String PATH = "/api/getThisSurvey";
    private final SurveyDao surveyDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;

    public SurveyGetSpecificController(SurveyDao surveyDao, QuestionDao questionDao, AnswerDao answerDao) {
        this.surveyDao = surveyDao;
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException {

        QueryString parameters = new QueryString(request.getMessageBody());

        String chosenSurvey = parameters.getParameter("survey");
        String chosenSurveyDecoded = URLDecoder.decode(chosenSurvey, StandardCharsets.UTF_8);

        Long surveyId = surveyDao.listBySurveyName(chosenSurveyDecoded).get(0).getId();

        String questionsAndAnswers = "";
        questionsAndAnswers = getString(questionsAndAnswers, surveyId);

        String response =   "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "<meta charset=\"UTF-8\">\n" +
                            "<style>body {\n" +
                            "background-color: #ffb365;\n" +
                            "font-family: \"MS Reference Sans Serif\", sans-serif;\n" +
                            "} main {\n" +
                            "padding: 5em;\n" +
                            "background-color: aliceblue;\n" +
                            "max-width: 20em;\n" +
                            "margin: auto;\n" +
                            "border-radius: .5em;\n" +
                            "}button {\n" +
                            "border-radius: 5px;\n" +
                            "}</style>" +
                            "<title>" + chosenSurveyDecoded + "</title>\n" +
                            "</head><body>" + "<main><br><form method='POST' action='/api/completedSurvey'" +
                "<label for='chosenSurvey'>Survey</label>" +
        "<br><select name='chosenSurvey' id='chosenSurvey'>" +
                "<option value='" + chosenSurveyDecoded + "'>" + chosenSurveyDecoded + "</option></select><div>\n" +
                "<label for='user-selection'>Choose user</label>\n" +
                "<br>\n" +
                "<select id=\"user-selection\" name=\"user\">\n" +
                "</select>\n" +
                "</div>" +
                            questionsAndAnswers +
                            "<button class='clickme success' type='submit'>Submit</button></form></main>" +
                            "<br>" + "</body><script>fetch(\"/api/user\")\n" +
                            ".then(function(response) {\n" +
                            "return response.text();\n" +
                            "}).then(function(text) {\n" +
                            "document.getElementById(`user-selection`).innerHTML = text;\n" +
                            "});</script>" +
                            "</html>";

        return new HttpMessage("HTTP/1.1 200 OK", response);
    }

    private String getString(String questionsResponse, Long surveyId) throws SQLException, UnsupportedEncodingException {
        int index = 0;
        for (Question question : questionDao.listQuestionsBySurveyId(surveyId)) {
            questionsResponse += "<p>" + question.getName() + "</p>" +
                    "<select id='answers'" + index + " name='answers'>" +
                    answerDao.findAllAnswers(question.getId()) +
                    "</select><br><p>------------------------------------" + "</p>";
            index++;
        }
        return questionsResponse;
    }
}

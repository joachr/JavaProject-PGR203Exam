package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.survey.Question;
import no.kristiania.exam.survey.QuestionDao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class QuestionEditController implements HttpController {

    private final QuestionDao questionDao;
    public static final String PATH = "/api/editQuestions";

    public QuestionEditController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException, IOException {
        String response = "";

        for (Question question : questionDao.listAll()) {
            response += "<option value='" + (question.getName()) + "'>" + question.getName() + "</option>";
        }
        return new HttpMessage("HTTP/1.1 200 OK", response);
    }
}

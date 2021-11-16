package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.survey.User;
import no.kristiania.exam.survey.UserDao;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class UserGetController implements HttpController {

    public static final String PATH = "/api/user";
    private final UserDao userDao;

    public UserGetController(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException, IOException {
        String response = "";

        for (User user : userDao.listAll()) {
            response += "<option value='" + (user.getFirstName()) + "'>" + user.getFirstName() + "</option>";
        }

        return new HttpMessage("HTTP/1.1 200 OK", response);
    }
}

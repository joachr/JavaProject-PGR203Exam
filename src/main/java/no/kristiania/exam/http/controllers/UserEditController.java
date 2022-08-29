package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.survey.User;
import no.kristiania.exam.survey.UserDao;

import java.io.IOException;
import java.sql.SQLException;

public class UserEditController implements HttpController {
    private final UserDao userDao;
    public static final String PATH = "/api/editUser";

    public UserEditController(UserDao userDao) {
        this.userDao = userDao;
    }

    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        String response = "";

        for (User user : userDao.listAll()) {
            response += "<option value='" + (user.geteMail()) + "'>" + user.geteMail() + "</option>";
        }

        return new HttpMessage("HTTP/1.1 200 OK", response);
    }


}

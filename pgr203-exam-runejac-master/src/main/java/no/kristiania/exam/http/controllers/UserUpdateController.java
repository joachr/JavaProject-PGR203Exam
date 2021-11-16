package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import no.kristiania.exam.http.HttpServer;
import no.kristiania.exam.http.QueryString;
import no.kristiania.exam.survey.User;
import no.kristiania.exam.survey.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class UserUpdateController implements HttpController{
    private final UserDao userDao;
    public static final String PATH = "/api/updateUser";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public UserUpdateController(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        QueryString parameters = new QueryString(request.getMessageBody());

        String firstName = parameters.getParameter("firstname_user");
        String lastName = parameters.getParameter("lastname_user");
        String email = parameters.getParameter("email");
        String user = parameters.getParameter("user");

        String decodedFirstName = URLDecoder.decode(firstName, StandardCharsets.UTF_8);
        String decodedLastName = URLDecoder.decode(lastName, StandardCharsets.UTF_8);
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        String decodedUser = URLDecoder.decode(user, StandardCharsets.UTF_8);

        Long userId = userDao.retrieveUserIdByMail(decodedUser);

        User userToUpdate = userDao.retrieveUsers(userId);

        userToUpdate.setFirstName(decodedFirstName);
        userToUpdate.setLastName(decodedLastName);
        userToUpdate.seteMail(decodedEmail);
        userDao.update(userToUpdate, userId);

        logger.info("User: " + "'" + userToUpdate.getFirstName() + "'" + " has been updated in the database");

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/newUser.html");
        return httpMessage;
    }


}

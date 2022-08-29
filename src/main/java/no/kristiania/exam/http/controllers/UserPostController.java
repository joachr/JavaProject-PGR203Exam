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
import java.util.Map;

public class UserPostController implements HttpController{
    private final UserDao userDao;
    public static final String PATH = "/api/newUser";
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public UserPostController(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        QueryString parameters = new QueryString(request.getMessageBody());

        String firstName = parameters.getParameter("firstname_user");
        String lastName = parameters.getParameter("lastname_user");
        String email = parameters.getParameter("email");

        String decodedFirstName = URLDecoder.decode(firstName, StandardCharsets.UTF_8);
        String decodedLastName = URLDecoder.decode(lastName, StandardCharsets.UTF_8);
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);

        User user = new User();
        user.setFirstName(decodedFirstName);
        user.setLastName(decodedLastName);
        user.seteMail(decodedEmail);
        for (String emailAddress : userDao.listAllEmails()) {
            if (emailAddress.equals(decodedEmail)) {
                logger.info("A user with this email address already exists");
                HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
                httpMessage.getHeaderFields().put("Location:", "/newUser.html");
                return httpMessage;
            }
        }
        userDao.saveUsers(user);
        logger.info("User: " + "'" + user.getFirstName() + "'" + " has been added to the database");

        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 303 See also");
        httpMessage.getHeaderFields().put("Location:", "/newUser.html");
        return httpMessage;
    }
}

package no.kristiania.exam.http;

import no.kristiania.exam.http.controllers.*;
import no.kristiania.exam.survey.*;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class QuestionnaireServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) throws IOException {
        DataSource dataSource = createDataSource();
        UserDao userDao = new UserDao(dataSource);
        SurveyDao surveyDao = new SurveyDao(dataSource);
        QuestionDao questionDao = new QuestionDao(dataSource);
        AnswerDao answerDao = new AnswerDao(dataSource);
        HttpServer server = new HttpServer(9090);
        server.setRoot(Paths.get("src/main/resources"));

        server.addController("/", new RedirectController("index.html", "index.html"));
        server.addController(QuestionGetController.PATH, new QuestionGetController(questionDao));
        server.addController(QuestionsWithAnswersGetController.PATH, new QuestionsWithAnswersGetController(questionDao, answerDao));
        server.addController(QuestionPostController.PATH, new QuestionPostController(questionDao));
        server.addController(SurveyGetController.PATH, new SurveyGetController(surveyDao));
        server.addController(OptionPostController.PATH, new OptionPostController(answerDao, questionDao));
        server.addController(SurveyPostController.PATH, new SurveyPostController(surveyDao, questionDao));
        server.addController(UserPostController.PATH, new UserPostController(userDao));
        server.addController(UserGetController.PATH, new UserGetController(userDao));
        server.addController(SurveyGetSpecificController.PATH, new SurveyGetSpecificController(surveyDao, questionDao, answerDao));
        server.addController(SurveyCompletePostController.PATH, new SurveyCompletePostController(surveyDao, userDao));
        server.addController(QuestionDeleteController.PATH, new QuestionDeleteController(questionDao, answerDao));
        server.addController(SurveyDeleteController.PATH, new SurveyDeleteController(surveyDao, questionDao));
        server.addController(UserUpdateController.PATH, new UserUpdateController(userDao));
        server.addController(UserEditController.PATH, new UserEditController(userDao));
        server.addController(QuestionUpdateController.PATH, new QuestionUpdateController(questionDao));
        server.addController(QuestionEditController.PATH, new QuestionEditController(questionDao));

        logger.info("Starting http://localhost:{}/index.html", server.getPort());
    }

    private static DataSource createDataSource() throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")){
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty(
                "dataSource.url",
                "jdbc:postgresql://localhost:5432/exam_survey_db"));
        dataSource.setUser(properties.getProperty(
                "dataSource.user",
                "exam_user"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }
}

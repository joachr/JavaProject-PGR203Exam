package no.kristiania.exam.survey;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SurveyDao {

    private final DataSource dataSource;
    private QuestionDao questionDao;

    public SurveyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveSurvey(Survey survey) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into survey (name, number_of_questions) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, survey.getName());
                statement.setInt(2, survey.getNumberOfQuestions());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    survey.setId(rs.getLong("id"));
                }
            }
        }
    }

    public int questionCounter(Survey survey) throws SQLException, UnsupportedEncodingException {
        questionDao = new QuestionDao(dataSource);
        int sum = 0;
        for (Question q : questionDao.listQuestionsBySurveyId(survey.getId())) {
            sum += 1;
        }
        survey.setNumberOfQuestions(sum);
        return sum;
    }

    public Survey retrieveSurvey(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from survey where id = ?"
            )) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return getSurvey(rs);
                }
            }
        }
    }

    private Survey getSurvey(ResultSet rs) throws SQLException {
        Survey survey = new Survey();
        survey.setName(rs.getString("name"));
        survey.setId(rs.getLong("id"));
        survey.setNumberOfQuestions(rs.getInt("number_of_questions"));

        return survey;
    }

    public ArrayList<Survey> listBySurveyName(String surveyName) throws SQLException, UnsupportedEncodingException {
        ArrayList<Survey> surveyArrayList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from survey where name = ?"
            )) {
                String decoded = URLDecoder.decode(surveyName, StandardCharsets.UTF_8.name());
                statement.setString(1, decoded);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        getSurvey(rs);
                        surveyArrayList.add(getSurvey(rs));
                    }
                    return surveyArrayList;
                }
            }
        }
    }

    public List<Survey> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from survey")) {
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Survey> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(getSurvey(rs));
                    }
                    return result;
                }
            }
        }
    }

    public void setNumOfQuestionsOnSurvey(int questionCounter, Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update survey set number_of_questions = ? where id = ?"
            )) {
                statement.setLong(1, questionCounter);
                statement.setLong(2, id);
                statement.executeUpdate();
            }
        }
    }

    public Long retrieveSurveyIdByName(String decodedSurveyName) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from survey where name = ?"
            )) {
                String decoded = URLDecoder.decode(decodedSurveyName, StandardCharsets.UTF_8.name());
                statement.setString(1, decoded);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return getSurvey(rs).getId();
                }
            }
        }
    }

    public void deleteSurvey(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from survey where id = ?"
            )) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }
    }
}

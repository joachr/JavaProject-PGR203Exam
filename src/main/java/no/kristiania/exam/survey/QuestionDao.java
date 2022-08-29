package no.kristiania.exam.survey;

import org.postgresql.util.PSQLException;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.List;

public class QuestionDao extends AbstractDao<Question> {

    public QuestionDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Question mapFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setName(rs.getString("question_name"));
        question.setId(rs.getLong("id"));
        question.setSurveyId(rs.getLong("survey_id"));
        question.setNumberOfAnswers(rs.getInt("number_of_answers"));

        return question;
    }

    @Override
    protected void insertObject(Question question, PreparedStatement statement) throws SQLException {
        statement.setString(1, question.getName());
        statement.setInt(2, question.getNumberOfAnswers());
    }

    public void saveQuestion(Question question) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into question (question_name, number_of_answers) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, question.getName());
                statement.setInt(2, question.getNumberOfAnswers());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    question.setId(rs.getLong("id"));
                }
            }
        }
    }

    public int answerCounter(Question question) throws SQLException {
        AnswerDao answerDao = new AnswerDao(dataSource);
        int sum = 0;
        for (Answer a : answerDao.listAnswerByQuestionId(question.getId())) {
            sum += 1;
        }
        question.setNumberOfAnswers(sum);
        return sum;
    }

    public Question retrieveQuestion(Long id) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from question where id = ?"
            )) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return mapFromResultSet(rs);
                }
            }
        }
    }

    public List<Question> listQuestionsBySurveyId(Long surveyId) throws SQLException, UnsupportedEncodingException {
        ArrayList<Question> questionArrayList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from question where survey_id = ?"
            )) {
                statement.setLong(1, surveyId);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        mapFromResultSet(rs);
                        questionArrayList.add(mapFromResultSet(rs));
                    }
                    return questionArrayList;
                }
            }
        }
    }

    public List<Question> listAll() throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from question")) {
                try (ResultSet rs = statement.executeQuery()) {

                    ArrayList<Question> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapFromResultSet(rs));
                    }
                    return result;
                }
            }
        }
    }

    public Long retrieveIdByName(String questionName) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from question where question_name = ?"
            )) {
                String decoded = URLDecoder.decode(questionName, StandardCharsets.UTF_8.name());
                statement.setString(1, decoded);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return mapFromResultSet(rs).getId();
                }
            }
        }
    }

    public void setSurveyIdByQuestionId(Long questions, Long surveyId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update question set survey_id = ? where id = ?"
            )) {
                statement.setLong(1, surveyId);
                statement.setLong(2, questions);
                statement.executeUpdate();
            }
        }
    }

    public void setNumOfAnswers(int answerCounter, Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update question set number_of_answers = ? where id = ?"
            )) {
                statement.setLong(1, answerCounter);
                statement.setLong(2, id);
                statement.executeUpdate();
            }
        }
    }

    public void deleteQuestion(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from question where id = ?"
            )) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }
    }

    public void setSurveyIdToNull(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update question set survey_id = null where survey_id = ?"
            )) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }
    }

    public void update(Question question, Long id) throws SQLException {
        update(question, "update question set question_name = (?) where id = (?)", id, 2);
    }

    public Question retrieveById(Long id) throws SQLException {
        return retrieveById(id, "select * from question where id = ?");
    }
}

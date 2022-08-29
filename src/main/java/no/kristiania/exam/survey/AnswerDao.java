package no.kristiania.exam.survey;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDao {

    private final DataSource dataSource;

    public AnswerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveAnswer(Answer answer) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into answer (name, question_id) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, answer.getName());
                statement.setLong(2, answer.getQuestionId());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    answer.setId(rs.getLong("id"));
                }
            }
        }
    }

    public ArrayList<Answer> listAnswerByQuestionId(Long questionId) throws SQLException {
        ArrayList<Answer> answerArrayList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from answer where question_id = ?"
            )) {
                statement.setLong(1, questionId);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        getAnswer(rs);
                        answerArrayList.add(getAnswer(rs));
                    }
                    return answerArrayList;
                }
            }
        }
    }

    private Answer getAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setName(rs.getString("name"));
        answer.setId(rs.getLong("id"));
        answer.setQuestionId(rs.getLong("question_id"));

        return answer;
    }

    public List<Answer> listAll(Long questionId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from answer where question_id = ?")) {
                statement.setLong(1, questionId);
                try (ResultSet rs = statement.executeQuery()) {
                    ArrayList<Answer> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(getAnswer(rs));
                    }
                    return result;
                }
            }
        }
    }

    public String findAllAnswers(Long id) throws SQLException {
        String response = "";
        int index = 0;
        for (Answer answer : listAll(id)) {
           response += "<option value='answers' name='answers'" + index + ">" +
                   answer.getName() + "</option>";
        }
        return response;
    }

    public void deleteAnswersByQuestionId(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "delete from answer where question_id = ?"
            )) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        }
    }

}

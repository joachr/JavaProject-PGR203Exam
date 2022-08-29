package no.kristiania.exam.survey;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends AbstractDao<User> {

    public UserDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected User mapFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.seteMail(rs.getString("email"));
        user.setId(rs.getLong("id"));
        user.setSurveyDone(rs.getLong("last_survey_done"));

        return user;
    }

    @Override
    protected void insertObject(User user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.geteMail());
    }

    public void saveUsers(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into users (first_name, last_name, email) values " +
                            "(?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.getFirstName());
                statement.setString(2, user.getLastName());
                statement.setString(3, user.geteMail());
                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    user.setId(rs.getLong("id"));
                }
            }
        }
    }

    public User retrieveUsers(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users where id = ?"
            )) {
                statement.setLong(1, id);

                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return getUser(rs);
                }
            }
        }
    }

    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.seteMail(rs.getString("email"));
        user.setId(rs.getLong("id"));
        user.setSurveyDone(rs.getLong("last_survey_done"));

        return user;
    }

    public ArrayList<User> listByUsersName(String firstName) throws SQLException, UnsupportedEncodingException {
        ArrayList<User> surveyArrayList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users where first_name = ?"
            )) {

                String decoded = URLDecoder.decode(firstName, StandardCharsets.UTF_8.name());
                statement.setString(1, decoded);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        getUser(rs);
                        surveyArrayList.add(getUser(rs));
                    }
                    return surveyArrayList;
                }
            }
        }
    }

    public List<User> listAll() throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users")) {
                try (ResultSet rs = statement.executeQuery()) {

                    ArrayList<User> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(getUser(rs));
                    }
                    return result;
                }
            }
        }
    }

    public ArrayList<String> listAllEmails() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users")) {
                try (ResultSet rs = statement.executeQuery()) {

                    ArrayList<String> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(getUser(rs).geteMail());
                    }
                    return result;
                }
            }
        }
    }

    public Long retrieveUserIdByName(String user) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users where first_name = ?"
            )) {
                statement.setString(1, user);
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return getUser(rs).getId();
                }
            }
        }

    }

    public Long retrieveUserIdByMail(String mail) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "select * from users where email = ?"
            )) {
                statement.setString(1, mail);
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    return getUser(rs).getId();
                }
            }
        }
    }

    public void setSurveyId(Long surveyId, Long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "update users set last_survey_done = ? where id = ?"
            )) {
                statement.setLong(1, surveyId);
                statement.setLong(2, userId);
                statement.executeUpdate();
            }
        }
    }

    public void update(User user, Long id) throws SQLException {
        update(user, "UPDATE users SET first_name = (?), last_name = (?), email = (?) WHERE id = (?)", id, 4);
    }
}

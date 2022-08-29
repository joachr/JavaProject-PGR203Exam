package no.kristiania.exam.survey;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDao<T> {

    protected final DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(T object, String sql, Long id, int idIndex) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement insertStatement = connection.prepareStatement(sql)) {
                insertObject(object, insertStatement);
                insertStatement.setLong(idIndex, id);
                insertStatement.executeUpdate();
            }
        }
    }

    protected T retrieveById(Long id, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    protected abstract T mapFromResultSet(ResultSet rs) throws SQLException;
    protected abstract void insertObject(T obj, PreparedStatement statement) throws SQLException;
}

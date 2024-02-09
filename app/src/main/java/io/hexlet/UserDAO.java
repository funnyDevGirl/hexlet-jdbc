package io.hexlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection conn) {
        connection = conn;
    }

    public void save(User user) throws SQLException {
        // Если пользователь новый, выполняем вставку
        // Иначе обновляем
        if (user.getId() == null) {
            var sql = "INSERT INTO users (username, phone) VALUES (?, ?)";
            try (var preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhone());
                preparedStatement.executeUpdate();
                var generatedKeys = preparedStatement.getGeneratedKeys();
                // Если идентификатор сгенерирован, извлекаем его и добавляем в сохраненный объект
                if (generatedKeys.next()) {
                    // Обязательно устанавливаем id в сохраненный объект
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        } else {
            // Здесь код обновления существующей записи

            /*var sql2 = "ALTER TABLE users RENAME COLUMN ? TO ?";
            try (var stmt = connection.prepareStatement(sql2)) {
                stmt.executeUpdate();
            }*/
        }
    }

    // Возвращается Optional<User>
    // Это упрощает обработку ситуаций, когда в базе ничего не найдено
    public Optional<User> find(Long id) throws SQLException {
        var sql = "SELECT * FROM users WHERE id = ?";
        try (var stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var username = resultSet.getString("username");
                var phone = resultSet.getString("phone");
                var user = new User(username, phone);
                user.setId(id);
                return Optional.of(user);
            }
            return Optional.empty();
        }
    }

    public void remove(User user) throws SQLException {
        if (user.getId() == null) {
            return;
        }
        if (user.getId() != null) {
            var sql3 = "DELETE FROM users WHERE id = ?";
            try (var statement = connection.prepareStatement(sql3)) {
                statement.setLong(1, user.getId());
                statement.executeUpdate();
            }
        }
    }
}
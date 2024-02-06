package io.hexlet;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public static void main(String[] args) throws SQLException {

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:hexlet_test")) {

            var sql = "CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(255), phone VARCHAR(255))";
            try (var statement = conn.createStatement()) {
                statement.execute(sql);
            }

            //данные перед вставкой нужно экранировать с помощью подготовленных запросов:
            var sql2 = "INSERT INTO users (username, phone) VALUES (?, ?)";

            //можно получить идентификатор вставленной записи и потом использовать его в коде:
            try (var preparedStatement = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, "Tommy");
                preparedStatement.setString(2, "33333333");
                preparedStatement.executeUpdate();


                preparedStatement.setString(1, "Maria");
                preparedStatement.setString(2, "44444444");
                preparedStatement.executeUpdate();


                var generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    System.out.println(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("DB have not returned an id after saving the entity");
                }
            }

            var sql3 = "DELETE FROM users WHERE username = 'Maria'";
            try (var statement = conn.prepareStatement(sql3)) {
                statement.executeUpdate();
            }

            var sql4 = "SELECT * FROM users";
            try (var statement2 = conn.createStatement()) {
                var resultSet = statement2.executeQuery(sql4);
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("username"));
                    System.out.println(resultSet.getString("phone"));
                }
            }
        }
    }
}

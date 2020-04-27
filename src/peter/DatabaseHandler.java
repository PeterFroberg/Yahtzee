package peter;

import java.sql.*;

public class DatabaseHandler {

    private Connection dbConnection;
    private PreparedStatement sqlStatement;

    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.15.22/yahtzee", "yuser", "kjh/(¤987JKH");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getScoreBoard(int playerID) {

    }

    public Player login(String email, String password) {
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM players WHERE email = ? and password = ?");
            sqlStatement.setString(1, email);
            sqlStatement.setString(2, password);
            ResultSet resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                Player player = new Player();
                player.setID(resultSet.getInt("ID"));
                player.setName((resultSet.getString("name")));
                player.setEmail(resultSet.getString("email"));
                return player;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertPlayer(String name, String email, String password) {
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO players (name, email, password) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            sqlStatement.setString(1, name);
            sqlStatement.setString(2, email);
            sqlStatement.setString(3, password);
            sqlStatement.executeUpdate();
            ResultSet rs = sqlStatement.getGeneratedKeys();
            if (rs.next()) {
                int newDbId = rs.getInt(1);
                return newDbId;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("duplicate user");
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

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

    public int CreateNewPlayer(String name, String email, String password) {
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

    public int invitePlayers(String invitingPlayer, String[] invitedPlayers) {
        int newGameID = 0;
        //Create GAME
        createGame(invitedPlayers.length + 1);
        try {
            //ADD Players to invitationTable
            sqlStatement = dbConnection.prepareStatement("INSERT INTO invitedplayers (invitedEmail, invitedToGame) VALUES(?,?)");
            for (String player : invitedPlayers) {
                sqlStatement.setString(1, player);
                sqlStatement.setInt(2, newGameID);
                sqlStatement.addBatch();
            }
            sqlStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newGameID;
    }

    public void joinGame(int playerID, String playerEmail, int gameID) {
        //CHECK invitation in invited tabel
        if(checkPlayerInvitedToGame(gameID, playerEmail)){

        }

        //IF invited

        //add Player to game
    }

    private int createGame(int numberOfPlayers) {
        int newGameID = 0;
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO games (numberOfPlayers) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            sqlStatement.setInt(1, numberOfPlayers);
            sqlStatement.executeUpdate();
            ResultSet rs = sqlStatement.getGeneratedKeys();
            if (rs.next()) {
                newGameID = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newGameID;
    }

    //ADD Player to game
    private void addPlayerToGame(int gameID, int playerID) {
        int max_position = getMaxPositionInGame( gameID);
        int expectedNumberOfPlayersInGame = getExpectedNumberOfPlayersInGame(gameID);
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO playinggame (playerID, positionInGame, stateOfGame) VALUES(?,?,?)");
            sqlStatement.setInt(1, playerID);
            sqlStatement.setInt(2, max_position);
            if(max_position - expectedNumberOfPlayersInGame == 1) {
                sqlStatement.setString(3, "playing");
            }else{
                sqlStatement.setString(3, "waiting for players");
            }

            sqlStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayerInvitedToGame (int gameID, String playerEmail){
        try {
            sqlStatement =dbConnection.prepareStatement("SELECT * FROM invitedplayers where gameID = ? and invitedEmail = ?");
            sqlStatement.setInt(1, gameID);
            sqlStatement.setString(2, playerEmail);
            ResultSet resultSet = sqlStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ResultSet getPlayerInGame(int gameID){
        ResultSet resultSet = null;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM playinggame WHERE gameID = ?");
            sqlStatement.setInt(1, gameID);
            resultSet = sqlStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    private int getExpectedNumberOfPlayersInGame(int gameID){
        int numberOfExpectedPlayers = 0;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT numberOfPlayers as numberOfPlayers FROM games WHERE ID = ?");
            sqlStatement.setInt(1, gameID);
            ResultSet resultSet = sqlStatement.executeQuery();
            if(resultSet.next()){
                numberOfExpectedPlayers = resultSet.getInt("numberOfPlayers");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numberOfExpectedPlayers;
    }

    private int getMaxPositionInGame(int gameID){
        int max_position = 0;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT MAX as max_position FROM playinggame WHERE GAMEID = ?");
            sqlStatement.setInt(1, gameID);
            ResultSet resultSet = sqlStatement.executeQuery();
            if(resultSet.next()){
                max_position = resultSet.getInt("max_position");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return max_position;
    }



}

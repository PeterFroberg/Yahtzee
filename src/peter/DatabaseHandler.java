package peter;

import java.sql.*;

public class DatabaseHandler {

    private Connection dbConnection;
    private PreparedStatement sqlStatement;

    /**
     * Connect to the database
     */
    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.15.22/yahtzee", "yuser", "kjh/(¤987JKH");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnectDatabase() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeSQLStatement() {
        try {
            sqlStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getScoreBoard(int playerID) {

    }

    /**
     * Handels the login process
     *
     * @param email
     * @param password
     * @return returns the loggedin user object
     */
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
        }finally {
            closeSQLStatement();
        }
        return null;
    }

    /**
     * create a new player
     *
     * @param name
     * @param email
     * @param password
     * @return Return the new players ID in the database
     */
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
        }finally {
            closeSQLStatement();
        }
        return -1;
    }

    /**
     * Adds invited players to the invite list in the DB
     *
     * @param invitingPlayer
     * @param invitedPlayers
     * @return returns the gameID for the new game created in the method createGame
     */
    public int invitePlayers(String invitingPlayer, String[] invitedPlayers) {
        int newGameID = 0;
        //Create GAME
        newGameID = createGame(invitedPlayers.length + 1);
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
        }finally {
            closeSQLStatement();
        }
        return newGameID;
    }

    /**
     * Join player to a game when using the join game option in the game
     *
     * @param playerID
     * @param playerEmail
     * @param gameID
     * @return returns a text response for the user GUI
     */
    public String joinGame(int playerID, String playerEmail, int gameID) {
        //CHECK invitation in invited tabel
        String playerAdded = "";
        if (checkPlayerInvitedToGame(gameID, playerEmail)) {
            //IF invited //add Player to game
            playerAdded = addPlayerToGame(gameID, playerID);
        } else {
            playerAdded = "Unable to add you to the game! please check invitation for game ID";
        }
        return playerAdded;
    }

    public boolean checkGameStarted(int gameId){
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM games WHERE ID = ?");
            sqlStatement.setInt(1, gameId);
            ResultSet resultSet= sqlStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString("gameState").equals("start") || resultSet.getString("gameState").equals("playing")){
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * creates a new game in the DB
     *
     * @param numberOfPlayers - numbers of players invited to the game
     * @return returns the created gameID
     */
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
        }finally {
            closeSQLStatement();
        }
        return newGameID;
    }

    //ADD Player to game

    /**
     * Add player to the game
     *
     * @param gameID   - the game the player is joining
     * @param playerID - the player that joins the game
     * @return returns true if it is the last player needed to start the game, returns false if waiting for more players
     */
    public String addPlayerToGame(int gameID, int playerID) {
        int max_position = getMaxPositionInGame(gameID);
        int expectedNumberOfPlayersInGame = getExpectedNumberOfPlayersInGame(gameID);
        String allPlayersConnected = "";
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO playinggame (playerID, positionInGame, gameId) VALUES(?,?,?)");
            sqlStatement.setInt(1, playerID);
            sqlStatement.setInt(2, max_position + 1);
            sqlStatement.setInt(3, gameID);
            sqlStatement.executeUpdate();
            if (expectedNumberOfPlayersInGame - max_position == 1) {
                //sqlStatement.setString(3, "start");
                setGameState(gameID, "start");
                allPlayersConnected = "You are added to the game, All players connected the game will soon start";
            } else {
                //sqlStatement.setString(3, "waiting for players");
                setGameState(gameID, "waiting for players");
                allPlayersConnected = "You are added to the game, please wait for more players to connect.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            allPlayersConnected = "Unable to add you to the game!";
        } finally {
            closeSQLStatement();
        }
        return allPlayersConnected;
    }

    public boolean startGame(int gameId){
        try {
            String stateOfGame = "";
            sqlStatement = dbConnection.prepareStatement("SELECT gameState FROM games WHERE ID = ? ");
            sqlStatement.setInt(1, gameId);
            ResultSet resultSet = sqlStatement.executeQuery();
            if(resultSet.next()){
                stateOfGame = resultSet.getString("gameState");
            }
            if (stateOfGame.equals("start")){

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setGameState(int gameID, String newGameState){
        try {
            sqlStatement = dbConnection.prepareStatement("UPDATE games SET gameState = ? WHERE ID = ?");
            sqlStatement.setString(1,newGameState);
            sqlStatement.setInt(2, gameID);
            sqlStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checking if the player was invited to the specified game
     *
     * @param gameID      - gameID that the player wants to join
     * @param playerEmail - email that the invitation user have and will be checked if this email was in vited to the game
     * @return returns true if the player was invited to the game else returns false
     */
    private boolean checkPlayerInvitedToGame(int gameID, String playerEmail) {
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM invitedplayers where invitedToGame = ? and invitedEmail = ?");
            sqlStatement.setInt(1, gameID);
            sqlStatement.setString(2, playerEmail);
            ResultSet resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                removeInvitatation(gameID, playerEmail);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }
        return false;
    }

    /**
     * Removes the invitation from the DB
     *
     * @param gameID      -
     * @param playerEmail
     */
    private void removeInvitatation(int gameID, String playerEmail) {
        try {
            sqlStatement = dbConnection.prepareStatement("DELETE FROM invitedplayers where invitedToGame = ? and invitedEmail = ?");
            sqlStatement.setInt(1, gameID);
            sqlStatement.setString(2, playerEmail);
            sqlStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }
    }


    private ResultSet getPlayerInGame(int gameID) {
        ResultSet resultSet = null;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM playinggame WHERE gameID = ?");
            sqlStatement.setInt(1, gameID);
            resultSet = sqlStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }
        return resultSet;
    }

    /**
     * gets the expected number of players, how many players that was invited to the game
     *
     * @param gameID
     * @return - returns the number of players expected to join the game
     */
    private int getExpectedNumberOfPlayersInGame(int gameID) {
        int numberOfExpectedPlayers = 0;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT numberOfPlayers as numberOfPlayers FROM games WHERE ID = ?");
            sqlStatement.setInt(1, gameID);
            ResultSet resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                numberOfExpectedPlayers = resultSet.getInt("numberOfPlayers");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }
        return numberOfExpectedPlayers;
    }

    /**
     * get the highest player position in the game
     *
     * @param gameID
     * @return
     */
    private int getMaxPositionInGame(int gameID) {
        int max_position = 0;
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT MAX(positionInGame) max_position FROM playinggame WHERE GAMEID = ?");
            sqlStatement.setInt(1, gameID);
            ResultSet resultSet = sqlStatement.executeQuery();
            if (resultSet.next()) {
                max_position = resultSet.getInt("max_position");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }

        return max_position;
    }


}

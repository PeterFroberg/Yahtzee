/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

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

            dbConnection = DriverManager.getConnection(System.getenv("dbserver"), System.getenv("sqluser"), System.getenv("sqlpassword"));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close SQL Statment
     */
    private void closeSQLStatement() {
        try {
            sqlStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            //Check if user was found
            if (resultSet.next()) {
                //create Player object with info from database
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
    public Player CreateNewPlayer(String name, String email, String password) {
        Player player = new Player();
        player.setID(-1);
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO players (name, email, password) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            sqlStatement.setString(1, name);
            sqlStatement.setString(2, email);
            sqlStatement.setString(3, password);
            sqlStatement.executeUpdate();
            ResultSet rs = sqlStatement.getGeneratedKeys();

            if (rs.next()) {
                player = new Player();
                player.setID(rs.getInt(1));
                player.setName(name);
                player.setEmail(email);
                return player;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("duplicate user");
            player.setID(-2);
            return player ;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }

        return player;
    }

    /**
     * Adds invited players to the invite list in the DB
     *
     * @param invitedPlayers
     * @return returns the gameID for the new game created in the method createGame
     */
    public int invitePlayers(String[] invitedPlayers) {
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
        String playerAdded;
        //CHECK invitation in invited table
        if (checkPlayerInvitedToGame(gameID, playerEmail)) {
            playerAdded = addPlayerToGame(gameID, playerID);
        } else {
            playerAdded = "-1;;0;;Unable to add you to the game! please check invitation for game ID";
        }
        return playerAdded;
    }

    /**
     * Check if game state is started for the supplied gameID
     * @param gameId - GameID to check state for
     * @return - Returns true if state is start otherwise return false
     */
    public boolean checkGameStarted(int gameId){
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT * FROM games WHERE ID = ?");
            sqlStatement.setInt(1, gameId);
            ResultSet resultSet= sqlStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString("gameState").equals("start")){
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

    /**
     * Add player to the game
     *
     * @param gameID   - the game the player is joining
     * @param playerID - the player that joins the game
     * @return returns true if it is the last player needed to start the game, returns false if waiting for more players
     */
    public String addPlayerToGame(int gameID, int playerID) {
        int playerPosition = getMaxPositionInGame(gameID) + 1;
        int expectedNumberOfPlayersInGame = getExpectedNumberOfPlayersInGame(gameID);
        String playerConnected;
        try {
            sqlStatement = dbConnection.prepareStatement("INSERT INTO playinggame (playerID, positionInGame, gameId) VALUES(?,?,?)");
            sqlStatement.setInt(1, playerID);
            sqlStatement.setInt(2, playerPosition);
            sqlStatement.setInt(3, gameID);
            sqlStatement.executeUpdate();
            if (expectedNumberOfPlayersInGame - playerPosition == 0) {
                setGameState(gameID, "start");
                playerConnected = expectedNumberOfPlayersInGame + ";;" + playerPosition + ";;You are added to the game, All players connected, the game will soon start";
            } else {
                setGameState(gameID, "waiting for players");
                playerConnected = expectedNumberOfPlayersInGame + ";;" + playerPosition + ";;You are added to the game, please wait for more players to connect.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            playerConnected = "-1;;0;;Unable to add you to the game!";
        } finally {
            closeSQLStatement();
        }
        return playerConnected;
    }

    /**
     * Change the game state for the supplied GameID
     * @param gameID - GameID to change state for
     * @param newGameState - state to change the game to
     */
    public void setGameState(int gameID, String newGameState){
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

    public String getPlayersInGame(int gameID) {
        ResultSet resultSet = null;
        String playersInGame = "";
        try {
            sqlStatement = dbConnection.prepareStatement("SELECT players.name, playinggame.positionInGame FROM playinggame LEFT JOIN players on playinggame.playerID = players.id WHERE playinggame.gameid = ?");
            sqlStatement.setInt(1, gameID);
            resultSet = sqlStatement.executeQuery();
            while(resultSet.next()){
                playersInGame = playersInGame + resultSet.getString("name") + ";";
                playersInGame = playersInGame + resultSet.getInt("positionInGame") + ";";
            }
            playersInGame = playersInGame.substring(0, playersInGame.length() -1);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeSQLStatement();
        }
        return playersInGame;
    }

}

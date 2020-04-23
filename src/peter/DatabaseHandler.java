package peter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

    private Connection dbConnection;
    private PreparedStatement sqlStatement;

    public void ConnectToDatabase(){
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.15.22/yahtzee", "muser", "kjh/(¤987JKH");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getScoreBoard(int playerID){

    }

    public void getPlayer(int playerID){

    }
}

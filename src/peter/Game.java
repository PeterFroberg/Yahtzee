/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import java.util.ArrayList;

public class Game {
    private int ID = 0;
    private int positionInGame = -1;
    private int numberOfPlayers = 0;
    private int playersCompletedGame = 0;
    private boolean gameStarted = false;
    private ArrayList<String> playerNames = new ArrayList<>();

    Game(){
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean active) {
        this.gameStarted = active;
    }

    public int getPositionInGame() {
        return positionInGame;
    }

    public void setPositionInGame(int positionInGame) {
        this.positionInGame = positionInGame;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getPlayersCompletedGame() {
        return playersCompletedGame;
    }

    public void increasePlayersCompletedGame() {
        this.playersCompletedGame++;
    }

    public void addPlayerName(String playerName){
        playerNames.add(playerName);
    }
    public String getPlayerName(int index){
        return playerNames.get(index);
    }
}

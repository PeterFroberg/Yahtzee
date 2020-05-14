package peter;

import java.util.ArrayList;

public class Game {
    private int ID = 0;
    private int positionInGame = -1;
    private int numberOfPlayers = 0;
    private int playersCompletedGame = 0;
    private int currentTurn = 0;
    private boolean active = false;
    private boolean myturn = false;
    private ArrayList<String> otherPlayers =new ArrayList<>();

    Game(){
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isMyturn() {
        return myturn;
    }

    public void setMyturn(boolean myturn) {
        this.myturn = myturn;
    }

    public void addPlayer(String playerName){
        otherPlayers.add(playerName);
    }

    public String getPlayer(int index){
        return otherPlayers.get(index);
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

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getPlayersCompletedGame() {
        return playersCompletedGame;
    }

    public void increasePlayersCompletedGame() {
        this.playersCompletedGame++;
    }
}

package peter;

import java.awt.*;
import java.util.ArrayList;

public class Game {
    private int ID = 0;
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
}
package peter;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    public JMenuItem menuItemCreateNewPlayer = new JMenuItem("Create new player");
    public JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    public JMenuItem menuItemLogin = new JMenuItem("Login");
    public JMenuItem menuItemJoinGame = new JMenuItem("Join game");
    //private JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    public JMenuItem menuItemExit = new JMenuItem("Exit");

    public MenuBar(){
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_P);
        add(menu);

        menuItemCreateNewPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
        //menuItemCreateNewPlayer.addActionListener(actionEvent -> createNewUser());

        menuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));
        //menuItemLogin.addActionListener(actionEvent -> loginplayer());

        menuItemInvitePlayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
        menuItemInvitePlayers.setEnabled(false);
        //menuItemInvitePlayers.addActionListener(actionEvent -> invitePlayer());

        menuItemJoinGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_DOWN_MASK));
        menuItemJoinGame.setEnabled(false);
        //menuItemJoinGame.addActionListener(actionEvent -> joinGame());

//        menuItemScoreBoard.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)));
//        menuItemScoreBoard.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)));
        //menuItemExit.addActionListener(actionEvent -> System.exit(0));

        menu.add(menuItemCreateNewPlayer);
        menu.add(menuItemLogin);
        menu.add(menuItemInvitePlayers);
        menu.add(menuItemJoinGame);
        //menu.add(menuItemScoreBoard);
        menu.add(menuItemExit);
    }

    public void enabelMenuOptions(boolean value){
        menuItemInvitePlayers.setEnabled(value);
        menuItemJoinGame.setEnabled(value);
    }
}

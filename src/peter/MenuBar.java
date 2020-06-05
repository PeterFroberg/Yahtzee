/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {

    public JMenuItem menuItemCreateNewPlayer = new JMenuItem("Create new player");
    public JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    public JMenuItem menuItemLogin = new JMenuItem("Login");
    public JMenuItem menuItemJoinGame = new JMenuItem("Join game");
    public JMenuItem menuItemExit = new JMenuItem("Exit");

    public MenuBar(){
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_P);
        add(menu);

        menuItemCreateNewPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));

        menuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));

        menuItemInvitePlayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
        menuItemInvitePlayers.setEnabled(false);

        menuItemJoinGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_DOWN_MASK));
        menuItemJoinGame.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)));

        menu.add(menuItemCreateNewPlayer);
        menu.add(menuItemLogin);
        menu.add(menuItemInvitePlayers);
        menu.add(menuItemJoinGame);
        menu.add(menuItemExit);
    }

    public void enabelMenuOptions(boolean value){
        menuItemInvitePlayers.setEnabled(value);
        menuItemJoinGame.setEnabled(value);
    }
}

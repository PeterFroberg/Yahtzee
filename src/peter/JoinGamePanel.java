package peter;

import javax.swing.*;
import java.awt.*;

public class JoinGamePanel extends JPanel {

    private JTextField jTextFieldJoinGame = new JTextField(15);

    public JoinGamePanel(){
        JPanel leftJoinGamePanel = new JPanel();
        leftJoinGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        leftJoinGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftJoinGamePanel.add(new JLabel("Join game: "));

        JPanel centerJoinGamePanel = new JPanel();
        centerJoinGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        centerJoinGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldJoinGame.addAncestorListener(new RequestFocusListener());
        centerJoinGamePanel.add(jTextFieldJoinGame);

        add(leftJoinGamePanel);
        add(centerJoinGamePanel);
    }

    public String getJoinGame(){
        return jTextFieldJoinGame.getText();
    }
}

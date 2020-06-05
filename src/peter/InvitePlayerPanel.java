/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import javax.swing.*;
import java.awt.*;

public class InvitePlayerPanel extends JPanel {

    private JTextField jTextFieldInvitePlayers = new JTextField(45);

    public InvitePlayerPanel(){
        JPanel leftInvitePanel = new JPanel();
        leftInvitePanel.setLayout(new GridLayout(2, 2, 5, 5));
        leftInvitePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftInvitePanel.add(new JLabel("Invite players"));

        JPanel centerInvitePanel = new JPanel();
        centerInvitePanel.setLayout(new GridLayout(2, 2, 5, 5));
        centerInvitePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldInvitePlayers.addAncestorListener(new RequestFocusListener());
        centerInvitePanel.add(jTextFieldInvitePlayers);

        add(leftInvitePanel);
        add(centerInvitePanel);
    }

    public String getInviteedPlayers(){
        return jTextFieldInvitePlayers.getText();
    }
}

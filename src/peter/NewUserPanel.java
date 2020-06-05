/**
 * @author  Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since   2020-06-04
 */

package peter;

import javax.swing.*;
import java.awt.*;

public class NewUserPanel extends JPanel{
    private JTextField jTextFilednewUserInputName = new JTextField(45);
    private JTextField jTextFieldnewUserInputEmail = new JTextField(45);
    private JTextField jTextFieldnewUserInputPassword = new JPasswordField(45);

    /**
     * Creates a NewUserPanel, used for adding new users of the game
     */
    public NewUserPanel(){
        JPanel leftNewUserPanel = new JPanel();
        leftNewUserPanel.setLayout(new GridLayout(3, 2, 5, 5));
        leftNewUserPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        leftNewUserPanel.add(new JLabel("Namn: "));
        leftNewUserPanel.add(new JLabel("Email: "));
        leftNewUserPanel.add(new JLabel("Password"));


        JPanel centerUserPanel = new JPanel();
        centerUserPanel.setLayout(new GridLayout(3, 2, 5, 5));
        centerUserPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        centerUserPanel.add(jTextFilednewUserInputName);
        jTextFilednewUserInputName.addAncestorListener(new RequestFocusListener());
        centerUserPanel.add(jTextFieldnewUserInputEmail);
        centerUserPanel.add(jTextFieldnewUserInputPassword);

        add(leftNewUserPanel);
        add(centerUserPanel);
    }

    public String getNewUserName(){
        return jTextFilednewUserInputName.getText();
    }

    public String getNewUserEmail(){
        return jTextFieldnewUserInputEmail.getText();
    }

    public String getNewUserPassword(){
        return jTextFieldnewUserInputPassword.getText();
    }

    public void setNewUserPassword(String password){
        this.jTextFieldnewUserInputPassword.setText(password);
    }
}

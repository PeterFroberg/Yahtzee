package peter;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField jTextFieldLoginName = new JTextField(45);
    private JTextField jTextFieldLoginPassword = new JPasswordField(45);

    public LoginPanel(){
        JPanel leftLoginPanel = new JPanel();
        leftLoginPanel.setLayout(new GridLayout(3, 2, 5, 5));
        leftLoginPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftLoginPanel.add(new JLabel("E-mail: "));
        leftLoginPanel.add(new JLabel("Password: "));

        JPanel centerLoginPanel = new JPanel();
        centerLoginPanel.setLayout(new GridLayout(3, 2, 5, 5));
        centerLoginPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldLoginName.addAncestorListener(new RequestFocusListener());
        centerLoginPanel.add(jTextFieldLoginName);
        centerLoginPanel.add(jTextFieldLoginPassword);

        add(leftLoginPanel);
        add(centerLoginPanel);
    }

    public String getLoginName(){
        return jTextFieldLoginName.getText();
    }

    public String getLoginPassword(){
        return jTextFieldLoginPassword.getText();
    }

    public void setLoginPassword(String password){
        this.jTextFieldLoginPassword.setText(password);
    }

}

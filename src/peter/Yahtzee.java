package peter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Yahtzee extends JPanel {

    private Player player = new Player();

    /**
     * Create a databasehandler to take care of database communication
     */
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private static JMenuBar menuBar = new JMenuBar();

    /**
     * Create GUI components for the application
     */

    //Menu items
    private JMenuItem menuItemCreateNewPlayer = new JMenuItem("Create new player");
    private JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    private JMenuItem menuItemLogin = new JMenuItem("Login");
    private JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    private JMenuItem menuItemExit = new JMenuItem("Exit");

    //Textareas
    private JTextArea textAreaChatArea = new JTextArea("Detta är chat meddelanden");
    private JTextArea textAreaScore = new JTextArea("");
    private JTextArea jTextAreaChatInput = new JTextArea("Inmatning av chatt");

    //TExtfields
    private JTextField jTextFieldDiceResult1 = new JTextField("1");
    private JCheckBox jCheckBoxDiceResult1 = new JCheckBox();
    private JTextField jTextFieldDiceResult2 = new JTextField("2");
    private JCheckBox jCheckBoxDiceResult2 = new JCheckBox();
    private JTextField jTextFieldDiceResult3 = new JTextField("3");
    private JCheckBox jCheckBoxDiceResult3 = new JCheckBox();
    private JTextField jTextFieldDiceResult4 = new JTextField("4");
    private JCheckBox jCheckBoxDiceResult4 = new JCheckBox();
    private JTextField jTextFieldDiceResult5 = new JTextField("5");
    private JCheckBox jCheckBoxDiceResult5 = new JCheckBox();

    private JTextField jTextFilednewUserInputName = new JTextField(45);
    private JTextField jTextFieldnewUserInputEmail = new JTextField(45);
    private JTextField newUserInputPassword = new JPasswordField(45);

    private JTextField jTextFieldLoginName = new JTextField(45);
    private JTextField jTextFieldLoginPassword = new JPasswordField(45);

    //Buttons
    private JButton buttonSendChat = new JButton("Send");
    private JButton buttonRollAgain = new JButton("Roll dices");
    private JButton buttonSaveResult = new JButton("Save Result");

    public Yahtzee() {

        //Create Menu
        menuBar = createMenuBar();
        //Layout on frame
        setLayout(new BorderLayout());

        //Add panels to the frame
        add(mainTopPanel(), BorderLayout.NORTH);
        add(mainRightPanel(), BorderLayout.EAST);
        add(mainCenterPanel(), BorderLayout.CENTER);
    }

    private void loginplayer(){
        Boolean endLogin = false;
        while (!endLogin){
            int buttonPressed = JOptionPane.showConfirmDialog(null, loginPanel(),"User Login"
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if(buttonPressed == JOptionPane.OK_OPTION){
                databaseHandler.connectToDatabase();
                player = databaseHandler.login(jTextFieldLoginName.getText(), jTextFieldLoginPassword.getText());
                if(player != null){
                    jTextFieldLoginPassword.setText("");
                    endLogin = true;
                }
                JOptionPane.showMessageDialog(this, "Unable to Login! Either User dont exist, wrong username or wrong password!");

            }else{
                endLogin = true;
            }
        }
    }

    private void createNewUser() {
        Boolean endUserInput = false;
        while (!endUserInput) {
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, newUserPanel(), "New user Form : "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if (buttonPressed == JOptionPane.OK_OPTION) {
                databaseHandler.connectToDatabase();
                int newDBID = databaseHandler.insertPlayer(jTextFilednewUserInputName.getText(), jTextFieldnewUserInputEmail.getText(), newUserInputPassword.getText());
                if (newDBID != 0) {
                    player.setID(newDBID);
                    player.setName(jTextFilednewUserInputName.getText());
                    player.setEmail(jTextFieldnewUserInputEmail.getText());
                    player.setPassword(newUserInputPassword.getText());
                }else{
                    JOptionPane.showMessageDialog(this, "User already exists!, Use another email account");
                }
                menuItemInvitePlayers.setEnabled(true);
                menuItemScoreBoard.setEnabled(true);
            }
            endUserInput = true;
        }
    }

    private void invitePlayer(){

    }

    private JPanel mainTopPanel(){
        JPanel mainTopPanel = new JPanel(new GridLayout(2, 6));
        mainTopPanel.add(new JLabel("Dices"));
        jTextFieldDiceResult1.setEditable(false);
        jTextFieldDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jTextFieldDiceResult1);

        jTextFieldDiceResult2.setEditable(false);
        jTextFieldDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jTextFieldDiceResult2);

        jTextFieldDiceResult3.setEditable(false);
        jTextFieldDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jTextFieldDiceResult3);

        jTextFieldDiceResult4.setEditable(false);
        jTextFieldDiceResult4.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jTextFieldDiceResult4);

        jTextFieldDiceResult5.setEditable(false);
        jTextFieldDiceResult5.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jTextFieldDiceResult5);

        mainTopPanel.add(buttonRollAgain);

        mainTopPanel.add(new JLabel("Save this dice:"));
        mainTopPanel.add(jCheckBoxDiceResult1);
        jCheckBoxDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jCheckBoxDiceResult2);
        jCheckBoxDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jCheckBoxDiceResult3);
        jCheckBoxDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        mainTopPanel.add(jCheckBoxDiceResult4);
        jCheckBoxDiceResult4.setHorizontalAlignment(JCheckBox.CENTER);
        mainTopPanel.add(jCheckBoxDiceResult5);
        jCheckBoxDiceResult5.setHorizontalAlignment(JCheckBox.CENTER);

        mainTopPanel.setMinimumSize(new Dimension(0, 50));
        mainTopPanel.setPreferredSize(mainTopPanel.getMinimumSize());
        mainTopPanel.setBorder(BorderFactory.createLineBorder(Color.orange));

        mainTopPanel.add(buttonSaveResult);

        return mainTopPanel;
    }

    private JPanel mainCenterPanel(){
        JPanel mainCenterPanel = new JPanel(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createLineBorder(Color.green));

        return mainCenterPanel;
    }

    private JPanel mainRightPanel(){
        JPanel rightTopPanel = new JPanel(new BorderLayout());
        JPanel rightMiddelPanel = new JPanel(new BorderLayout());
        JPanel rightBottomPanel = new JPanel();

        JPanel mainRightPanel = new JPanel(new BorderLayout());
        mainRightPanel.add(rightTopPanel, BorderLayout.NORTH);
        mainRightPanel.add(rightMiddelPanel, BorderLayout.CENTER);
        mainRightPanel.add(rightBottomPanel, BorderLayout.SOUTH);

        rightTopPanel.add(textAreaChatArea);

        //Populate rightmiddel panel
        rightMiddelPanel.add(jTextAreaChatInput);

        //Populate rightbottom panel
        rightBottomPanel.add(buttonSendChat);

        mainRightPanel.setMinimumSize(new Dimension(180, 0));
        rightTopPanel.setMinimumSize(new Dimension(170, 350));
        rightMiddelPanel.setMinimumSize(new Dimension(170, 80));
        rightBottomPanel.setMinimumSize(new Dimension(170, 40));

        mainRightPanel.setPreferredSize(mainRightPanel.getMinimumSize());
        rightTopPanel.setPreferredSize(rightTopPanel.getMinimumSize());
        rightMiddelPanel.setPreferredSize((rightMiddelPanel.getMinimumSize()));
        rightBottomPanel.setPreferredSize(rightBottomPanel.getMinimumSize());

        mainRightPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        rightTopPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        rightMiddelPanel.setBorder(BorderFactory.createLineBorder(Color.cyan));
        rightBottomPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));

        return mainRightPanel;
    }

    private JPanel loginPanel(){
        JPanel loginPanel = new JPanel();

        JPanel leftLoginPanel = new JPanel();
        leftLoginPanel.setLayout(new GridLayout(3,2,5,5));
        leftLoginPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        leftLoginPanel.add(new JLabel("E-mail: "));
        leftLoginPanel.add(new JLabel("Password: "));

        JPanel centerLoginPanel = new JPanel();
        centerLoginPanel.setLayout(new GridLayout(3,2,5,5));
        centerLoginPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        centerLoginPanel.add(jTextFieldLoginName);
        centerLoginPanel.add(jTextFieldLoginPassword);

        loginPanel.add(leftLoginPanel);
        loginPanel.add(centerLoginPanel);

        return loginPanel;
    }

    private JPanel newUserPanel() {
        JPanel newUserPanel = new JPanel();

        JPanel centerUserPanel = new JPanel();
        centerUserPanel.setLayout(new GridLayout(4, 2, 5, 5));
        centerUserPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        centerUserPanel.add(new JLabel("Namn: "));
        centerUserPanel.add(jTextFilednewUserInputName);
        centerUserPanel.add(new JLabel("Email: "));
        centerUserPanel.add(jTextFieldnewUserInputEmail);
        centerUserPanel.add(new JLabel("Password"));
        centerUserPanel.add(newUserInputPassword);

        newUserPanel.add(centerUserPanel);

        return newUserPanel;
    }

    private JMenuBar createMenuBar(){
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Play");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);

        menuItemCreateNewPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));

        menuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));

        menuItemInvitePlayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
        menuItemInvitePlayers.setEnabled(false);

        menuItemScoreBoard.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK)));
        menuItemScoreBoard.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK)));

        //Set action Listeners to menu items
        menuItemCreateNewPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createNewUser();
            }
        });

        menuItemLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loginplayer();
            }
        });

        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        menu.add(menuItemCreateNewPlayer);
        menu.add(menuItemLogin);
        menu.add(menuItemInvitePlayers);
        menu.add(menuItemScoreBoard);
        menu.add(menuItemExit);

        return menuBar;
    }

    private static void createAndShowGUI() {

        Yahtzee mainPanel = new Yahtzee();

        JFrame frame = new JFrame("Peters online Yahtzee game!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}

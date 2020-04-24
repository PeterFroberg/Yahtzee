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
    private JPanel topPanel = new JPanel(new GridLayout(2, 6));
    private JPanel centerPanel = new JPanel();
    private JPanel rightTopPanel = new JPanel(new BorderLayout());
    private JPanel rightMiddelPanel = new JPanel(new BorderLayout());
    private JPanel rightBottomPanel = new JPanel();
    private JPanel bottomPanel = new JPanel();

    /**
     * Create GUI components for the application
     */

    //Menu items
    private static JMenuItem menuItemCreateNewPlayer = new JMenuItem("Create new player");
    private static JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    private JMenuItem menuItemLogin = new JMenuItem("Login");
    private static JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    private static JMenuItem menuItemExit = new JMenuItem("Exit");

    private static JTextArea textAreaChatArea = new JTextArea("Detta är chat meddelanden");
    private static JTextArea textAreaScore = new JTextArea("");
    private static JTextArea jTextAreaChatInput = new JTextArea("Inmatning av chatt");

    private static JTextField jTextFieldDiceResult1 = new JTextField("1");
    private static JCheckBox jCheckBoxDiceResult1 = new JCheckBox();
    private static JTextField jTextFieldDiceResult2 = new JTextField("2");
    private static JCheckBox jCheckBoxDiceResult2 = new JCheckBox();
    private static JTextField jTextFieldDiceResult3 = new JTextField("3");
    private static JCheckBox jCheckBoxDiceResult3 = new JCheckBox();
    private static JTextField jTextFieldDiceResult4 = new JTextField("4");
    private static JCheckBox jCheckBoxDiceResult4 = new JCheckBox();
    private static JTextField jTextFieldDiceResult5 = new JTextField("5");
    private static JCheckBox jCheckBoxDiceResult5 = new JCheckBox();

    private static JButton buttonSendChat = new JButton("Send");
    private static JButton buttonRollAgain = new JButton("Roll dices");
    private static JButton buttonSaveResult = new JButton("Save Result");

    private JTextField jTextFilednewUserInputName = new JTextField(45);
    private JTextField jTextFieldnewUserInputEmail = new JTextField(45);
    private JTextField newUserInputPassword = new JPasswordField(45);

    private JTextField jTextFieldLoginName = new JTextField(45);
    private JTextField jTextFieldLoginPassword = new JPasswordField(45);

    public Yahtzee() {

        //Create Menu
        menuBar = createMenuBar();


        JPanel outerCenterPanel = new JPanel(new BorderLayout());
        outerCenterPanel.add(centerPanel, BorderLayout.CENTER);
        outerCenterPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel outerRightPanel = new JPanel(new BorderLayout());
        outerRightPanel.add(rightTopPanel, BorderLayout.NORTH);
        outerRightPanel.add(rightMiddelPanel, BorderLayout.CENTER);
        outerRightPanel.add(rightBottomPanel, BorderLayout.SOUTH);
        //outerRightPanel.add(rightBottomPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        //Populate toppanel
        topPanel.add(new JLabel("Dices"));
        jTextFieldDiceResult1.setEditable(false);
        jTextFieldDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult1);

        jTextFieldDiceResult2.setEditable(false);
        jTextFieldDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult2);

        jTextFieldDiceResult3.setEditable(false);
        jTextFieldDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult3);

        jTextFieldDiceResult4.setEditable(false);
        jTextFieldDiceResult4.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult4);

        jTextFieldDiceResult5.setEditable(false);
        jTextFieldDiceResult5.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jTextFieldDiceResult5);

        topPanel.add(buttonRollAgain);

        topPanel.add(new JLabel("Save this dice:"));
        topPanel.add(jCheckBoxDiceResult1);
        jCheckBoxDiceResult1.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult2);
        jCheckBoxDiceResult2.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult3);
        jCheckBoxDiceResult3.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(jCheckBoxDiceResult4);
        jCheckBoxDiceResult4.setHorizontalAlignment(JCheckBox.CENTER);
        topPanel.add(jCheckBoxDiceResult5);
        jCheckBoxDiceResult5.setHorizontalAlignment(JCheckBox.CENTER);

        topPanel.add(buttonSaveResult);
        //populate Right top panel
        rightTopPanel.add(textAreaChatArea);

        //Populate rightmiddel panel
        rightMiddelPanel.add(jTextAreaChatInput);


        //Populate rightbottom panel
        rightBottomPanel.add(buttonSendChat);

        //Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(outerRightPanel, BorderLayout.EAST);
        add(outerCenterPanel, BorderLayout.CENTER);

        topPanel.setMinimumSize(new Dimension(0, 50));
        outerRightPanel.setMinimumSize(new Dimension(180, 0));
        rightTopPanel.setMinimumSize(new Dimension(170, 350));
        rightMiddelPanel.setMinimumSize(new Dimension(170, 80));
        rightBottomPanel.setMinimumSize(new Dimension(170, 40));
        bottomPanel.setMinimumSize(new Dimension(0, 350));

        topPanel.setPreferredSize(topPanel.getMinimumSize());
        outerRightPanel.setPreferredSize(outerRightPanel.getMinimumSize());
        rightTopPanel.setPreferredSize(rightTopPanel.getMinimumSize());
        rightMiddelPanel.setPreferredSize((rightMiddelPanel.getMinimumSize()));
        rightBottomPanel.setPreferredSize(rightBottomPanel.getMinimumSize());
        bottomPanel.setPreferredSize(bottomPanel.getMinimumSize());

        topPanel.setBorder(BorderFactory.createLineBorder(Color.orange));
        outerRightPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        rightTopPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        rightMiddelPanel.setBorder(BorderFactory.createLineBorder(Color.cyan));
        rightBottomPanel.setBorder(BorderFactory.createLineBorder(Color.yellow));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.green));
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
                }
                menuItemInvitePlayers.setEnabled(true);
                menuItemScoreBoard.setEnabled(true);
            }
            endUserInput = true;
        }
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

        JFrame frame = new JFrame("EmielGui");
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

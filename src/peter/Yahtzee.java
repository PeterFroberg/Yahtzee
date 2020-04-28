package peter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Yahtzee extends JPanel implements Runnable {

    private Player player = new Player();
    private final static String DEFAULTHOST = "127.0.0.1";
    private final static int DEFAULTPORT = 2000;

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;

    /**
     * Create a databasehandler to take care of database communication
     */
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private static JMenuBar menuBar = new JMenuBar();

    private static LinkedBlockingQueue<String> serverMessages = new LinkedBlockingQueue<String>();

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
    private static JTextArea jTextAreaTextAreaChatArea = new JTextArea("Detta är chat meddelanden");
    private JTextArea jTextAreaTextAreaScore = new JTextArea("");
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

    public Yahtzee(BufferedReader socketReader, PrintWriter socketWriter) {

        this.socketReader = socketReader;
        this.socketWriter = socketWriter;

        //Create Menu
        menuBar = createMenuBar();
        //Layout on frame
        setLayout(new BorderLayout());

        //Add panels to the frame
        add(mainTopPanel(), BorderLayout.NORTH);
        add(mainRightPanel(), BorderLayout.EAST);
        add(mainCenterPanel(), BorderLayout.CENTER);
    }

    /**
     * Enables/disabels swing components
     * @param value - true/false
     */
    private void enableOptions(boolean value) {
        menuItemScoreBoard.setEnabled(value);
        menuItemInvitePlayers.setEnabled(value);

        buttonSendChat.setEnabled(value);
    }

    /**
     * Login player on the server, shows a dialogbox for signing in
     */
    private void loginplayer() {
        boolean endLogin = false;
        while (!endLogin) {
            int buttonPressed = JOptionPane.showConfirmDialog(null, loginPanel(), "User Login"
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if (buttonPressed == JOptionPane.OK_OPTION) {
                player.setID(0);
                player.setName("");
                player.setEmail("");
                setEnabled(false);

                sendMessage("login::" + jTextFieldLoginName.getText() + ";;" + jTextFieldLoginPassword.getText());
                while (player.getID() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (player.getID() == -1){
                    JOptionPane.showMessageDialog(this, "Unable to Login! Either User dont exist, wrong username or wrong password!");
                }else{
                    jTextFieldLoginPassword.setText("");
                    enableOptions(true);
                    endLogin = true;
                    JOptionPane.showMessageDialog(this, "Welcomback " + player.getName() + "!");
                }


            } else {
                endLogin = true;
            }
        }
    }

    /**
     * Method to create a new user, send a message to the server to create the user
     * When the message is sent the the while loops waits until the player ID i set to other than
     * zero. If player ID is set to -1 the user already exits in the database
     * @param socketWriter - which Socketwriter to use for sending the message
     */
    private void createNewUser(PrintWriter socketWriter) {
        boolean endUserInput = false;
        while (!endUserInput) {
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, newUserPanel(), "New user Form : "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if (buttonPressed == JOptionPane.OK_OPTION) {
                sendMessage("new_user::" + jTextFilednewUserInputName.getText() + ";;" + jTextFieldnewUserInputEmail.getText() + ";;" + newUserInputPassword.getText());
                while (player.getID() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (player.getID() == -1) {
                    JOptionPane.showMessageDialog(this, "User already exists! Use another email account");
                } else {
                    enableOptions(true);
                }
            }
            endUserInput = true;
        }
    }

    /**
     * Send message to the game server
     * @param message - message to be sent
     */
    private void sendMessage(String message) {
        socketWriter.println("chatt::" + message);
    }

    private void invitePlayer() {

    }

    /**
     * creates the top panel and populates it with swing components
     * @return - returns the created JPanel
     */
    private JPanel mainTopPanel() {
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

    /**
     * creates the center panel and populates it with swing components
     * @return - returns the created JPanel
     */
    private JPanel mainCenterPanel() {
        JPanel mainCenterPanel = new JPanel(new BorderLayout());
        mainCenterPanel.setBorder(BorderFactory.createLineBorder(Color.green));

        return mainCenterPanel;
    }

    /**
     * creates the right panel and populates it with swing components
     * @return - returns the created JPanel
     */
    private JPanel mainRightPanel() {
        JPanel rightTopPanel = new JPanel(new BorderLayout());
        JPanel rightMiddelPanel = new JPanel(new BorderLayout());
        JPanel rightBottomPanel = new JPanel();

        JPanel mainRightPanel = new JPanel(new BorderLayout());
        mainRightPanel.add(rightTopPanel, BorderLayout.NORTH);
        mainRightPanel.add(rightMiddelPanel, BorderLayout.CENTER);
        mainRightPanel.add(rightBottomPanel, BorderLayout.SOUTH);

        jTextAreaTextAreaChatArea.setLineWrap(true);
        jTextAreaTextAreaChatArea.setWrapStyleWord(true);
        rightTopPanel.add(jTextAreaTextAreaChatArea);

        //Populate rightmiddel panel
        jTextAreaChatInput.setLineWrap(true);
        jTextAreaChatInput.setWrapStyleWord(true);
        rightMiddelPanel.add(jTextAreaChatInput);

        //Populate rightbottom panel
        buttonSendChat.addActionListener(actionEvent ->
                sendMessage(player.getName() + ": " + jTextAreaChatInput.getText()));
        buttonSendChat.setEnabled(false);
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

    /**
     * creates the Login panel and populates it with swing components
     * @return - returns the created JPanel
     */
    private JPanel loginPanel() {
        JPanel loginPanel = new JPanel();

        JPanel leftLoginPanel = new JPanel();
        leftLoginPanel.setLayout(new GridLayout(3, 2, 5, 5));
        leftLoginPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftLoginPanel.add(new JLabel("E-mail: "));
        leftLoginPanel.add(new JLabel("Password: "));

        JPanel centerLoginPanel = new JPanel();
        centerLoginPanel.setLayout(new GridLayout(3, 2, 5, 5));
        centerLoginPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerLoginPanel.add(jTextFieldLoginName);
        centerLoginPanel.add(jTextFieldLoginPassword);

        loginPanel.add(leftLoginPanel);
        loginPanel.add(centerLoginPanel);

        return loginPanel;
    }

    /**
     * creates the New user panel and populates it with swing components
     * @return - returns the created JPanel
     */
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

    /**
     * creates the menubar and populates it with swing components
     * @return - returns the created menubar
     */
    private JMenuBar createMenuBar() {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Play");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);

        menuItemCreateNewPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));

        menuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));

        menuItemInvitePlayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
        menuItemInvitePlayers.setEnabled(false);

        menuItemScoreBoard.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)));
        menuItemScoreBoard.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)));

        //Set action Listeners to menu items
        menuItemCreateNewPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createNewUser(socketWriter);
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

    /**
     * Setup communication with the server, Starts the GUI
     */
    private static void createAndShowGUI() {
        /**
         * Creates connection to server
         */
        Socket socket = null;
        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;
        BufferedReader consoleReader = null;

        try {
            socket = new Socket(DEFAULTHOST, DEFAULTPORT);
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to game server, please try again later");
            e.printStackTrace();
        }

        Yahtzee mainPanel = new Yahtzee(socketReader, socketWriter);

        /**
         * create a Receive thread
         */
        Thread receiveThread = new Thread(mainPanel);
        receiveThread.start();

        /**
         * Display GUI
         */
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

    @Override
    public void run() {
        try {
            /**
             * Keeps the receiver alive
             */
            while (true) {
                if (socketReader.ready()) {
                    String incommingMessage = socketReader.readLine();
                    String[] parts = incommingMessage.split("::");
                    String messageCode = parts[0];
                    String message = parts[1];
                    //GÖR OM TILL CASE/SWITCH
                    switch (messageCode) {
                        case "chatt":
                            jTextAreaTextAreaChatArea.setText(jTextAreaTextAreaChatArea.getText() + "\n" + message);
                            break;
                        case "new_user":
                            String[] newUserParts = message.split(";;");
                            if (!newUserParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(newUserParts[0]));
                                player.setName(jTextFilednewUserInputName.getText());
                                player.setEmail(jTextFieldnewUserInputEmail.getText());
                            } else {
                                player.setID(-1);
                            }
                            break;
                        case "login_user":
                            String[] loginUserParts = message.split(";;");
                            if(!loginUserParts[0].equals("-1")){
                                player.setID(Integer.valueOf(loginUserParts[0]));
                                player.setName(loginUserParts[1]);
                                player.setEmail(loginUserParts[2]);
                            }else{
                                player.setID(-1);
                            }
                            break;
                    }
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

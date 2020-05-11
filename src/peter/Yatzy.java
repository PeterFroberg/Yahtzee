package peter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Yatzy extends JPanel implements Runnable {

    private static final String[] LABELS = {"Aeces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper score", "Upper Bonus", "Upper total",
            "1 pair", " 2 pairs", "3 of a kind", "4 of a kind", "Full house", "Sm Straight", "Lg Straight", "Chance", "YATZY",
            "Grand total"};
    private static final int MAX_NUMBER_OF_DICE_ROLLS = 3;

    private int positionInGame = 0;
    private int nuberOfDicesRolled = 1;
    private ArrayList<Integer> dices = new ArrayList<>();
    private Map<String, Integer> calculatedScores = new HashMap<String, Integer>();

    private Player player = new Player();
    private Game game = new Game();
    private Map<String, JTextField> scoreBoardMap = new HashMap<String, JTextField>();

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
    private JMenuItem menuItemJoinGame = new JMenuItem("Join game");
    private JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    private JMenuItem menuItemExit = new JMenuItem("Exit");

    //Textareas
    private static JTextArea jTextAreaTextAreaChatArea = new JTextArea("Detta är chat meddelanden");
    private JTextArea jTextAreaChatInput = new JTextArea("Inmatning av chatt");

    //Textfields and checkboxes
    private JTextField jTextFieldDiceResult1 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult1 = new JCheckBox();
    private JTextField jTextFieldDiceResult2 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult2 = new JCheckBox();
    private JTextField jTextFieldDiceResult3 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult3 = new JCheckBox();
    private JTextField jTextFieldDiceResult4 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult4 = new JCheckBox();
    private JTextField jTextFieldDiceResult5 = new JTextField("-");
    private JCheckBox jCheckBoxDiceResult5 = new JCheckBox();

    private JTextField jTextFilednewUserInputName = new JTextField(45);
    private JTextField jTextFieldnewUserInputEmail = new JTextField(45);
    private JTextField newUserInputPassword = new JPasswordField(45);

    private JTextField jTextFieldLoginName = new JTextField(45);
    private JTextField jTextFieldLoginPassword = new JPasswordField(45);

    private JTextField jTextFieldInvitePlayers = new JTextField(45);

    private JTextField jTextFieldJoinGame = new JTextField(15);

    //Buttons
    private JButton buttonSendChat = new JButton("Send");
    private JButton buttonRollDices = new JButton("Roll dices");
    private JButton buttonSaveResult = new JButton("Save Result");

    //Dropdowns
    private JComboBox<String> comboBoxSaveOptions = new JComboBox<>(new String[]{});

    public Yatzy(BufferedReader socketReader, PrintWriter socketWriter) {

        this.socketReader = socketReader;
        this.socketWriter = socketWriter;

        //Create Menu
        menuBar = createMenuBar();
        //Layout on frame
        setLayout(new BorderLayout());

        //Add panels to the frame
        add(mainTopPanel(), BorderLayout.NORTH);
        add(mainRightPanel(), BorderLayout.EAST);
        //add(mainCenterPanel(), BorderLayout.CENTER);
        add(scoreBoardPanel(), BorderLayout.CENTER);
    }

    /**
     * Enables/disabels swing components
     *
     * @param value - true/false
     */
    private void enableOptions(boolean value) {
        menuItemScoreBoard.setEnabled(value);
        menuItemInvitePlayers.setEnabled(value);
        menuItemJoinGame.setEnabled(value);

        //buttonSendChat.setEnabled(value);
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
                if (player.getID() == -1) {
                    JOptionPane.showMessageDialog(this, "Unable to Login! Either User dont exist, wrong username or wrong password!");
                } else {
                    jTextFieldLoginPassword.setText("");
                    enableOptions(true);
                    endLogin = true;
                    JOptionPane.showMessageDialog(this, "Welcom back " + player.getName() + "!");
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
     */
    private void createNewUser() {
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
     *
     * @param message - message to be sent
     */
    private void sendMessage(String message) {
        socketWriter.println(message);
    }

    private void invitePlayer() {
        boolean endInviteInput = false;
        while (!endInviteInput) {
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, invitePlayerPanel(), "Invite players: "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if (buttonPressed == JOptionPane.OK_OPTION) {
                sendMessage("invite_players::" + player.getEmail() + ";" + player.getID() + ";" + jTextFieldInvitePlayers.getText());
            }
            endInviteInput = true;
        }
    }

    private void joinGame() {
        boolean endJoinGame = false;
        while (!endJoinGame) {
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, joinGamePanel(), "Join Game: "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            if (buttonPressed == JOptionPane.OK_OPTION) {
                sendMessage("join_game::" + player.getID() + ";" + player.getEmail() + ";" + jTextFieldJoinGame.getText());
            }
            endJoinGame = true;
        }

    }

    private void playTurn() {
        buttonRollDices.setEnabled(true);
        nuberOfDicesRolled = 1;
        JOptionPane.showMessageDialog(this, "It it your turn! Please roll the dices to start the turn.");
    }

    private void rollDices() {
        if (nuberOfDicesRolled <= MAX_NUMBER_OF_DICE_ROLLS) {
            sendMessage("roll_dices::na");
            buttonSaveResult.setEnabled(true);
            if (nuberOfDicesRolled == MAX_NUMBER_OF_DICE_ROLLS) {
                buttonRollDices.setEnabled(false);
            }
            nuberOfDicesRolled++;
        } else {
            buttonRollDices.setEnabled(false);
        }
    }

    private void putDicesInArray() {
        dices.add(Integer.parseInt(jTextFieldDiceResult1.getText()));
        dices.add(Integer.parseInt(jTextFieldDiceResult2.getText()));
        dices.add(Integer.parseInt(jTextFieldDiceResult3.getText()));
        dices.add(Integer.parseInt(jTextFieldDiceResult4.getText()));
        dices.add(Integer.parseInt(jTextFieldDiceResult5.getText()));
        System.out.println(dices);
        Collections.sort(dices);
        System.out.println(dices);
    }

    private void checkSingels() {
        for (int i = 1; i < 7; i++) {
            if (dices.contains(i)) {
                if (scoreBoardMap.get("P1" + LABELS[i - 1]).getText().equals("")) {
                    comboBoxSaveOptions.addItem(LABELS[i - 1]);
                    int sum = 0;
                    for (int dice : dices) {
                        if (dice == i) {
                            sum += i;
                        }
                    }
                    calculatedScores.put(LABELS[i - 1], sum);
                }
            }
        }
    }

    private void chkeckForPairs(ArrayList<Integer> occrencesOfDices) {
        int pair1Sum = 0;
        int pair2Sum = 0;

        for (int i = 5; i >= 0; i--) {
            if (occrencesOfDices.get(i) > 1 && pair1Sum != 0) {
                if(scoreBoardMap.get("P12 pairs").getText().equals("")) {
                    comboBoxSaveOptions.addItem(" 2 pairs");
                    pair2Sum = (i + 1) * 2;
                    calculatedScores.put("2 pair", pair2Sum);
                }
            }
            if (occrencesOfDices.get(i) > 1 && pair1Sum == 0) {
                if(scoreBoardMap.get("P11 pair").getText().equals("")) {
                    comboBoxSaveOptions.addItem("1 pair");
                    pair1Sum = (i + 1) * 2;
                    calculatedScores.put("1 pair", pair1Sum);
                }
            }
        }
    }

    private void checkCombinations() {
        ArrayList<Integer> occrencesOfDices = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            occrencesOfDices.add(Collections.frequency(dices, i));
        }
        chkeckForPairs(occrencesOfDices);
        System.out.println(occrencesOfDices);


    }

    private void updateUpperTotals(int score, int playerNumber) {
        JTextField upperScoreTextField = scoreBoardMap.get("P" + playerNumber + "Upper score");
        if (upperScoreTextField.getText().equals("")) {
            upperScoreTextField.setText("0");
        }
        int currentTotal = Integer.parseInt(upperScoreTextField.getText()) + score;
        upperScoreTextField.setText(String.valueOf(currentTotal));
        //scoreBoardMap.put("P" + playerNumber + "Upper score", upperScoreTextField);
        if (currentTotal > 62) {
            JTextField bonusTextField = scoreBoardMap.get("P" + playerNumber + "Upper Bonus");
            bonusTextField.setText("50");
            //scoreBoardMap.put("P" + playerNumber + "Upper Bonus", bonusTextField);
            currentTotal += 50;
        }
        JTextField upperTotal = scoreBoardMap.get("P" + playerNumber + "Upper total");
        upperTotal.setText(String.valueOf(currentTotal));
        //scoreBoardMap.put("P" + playerNumber + "Upper total", upperTotal);
        updateLowerTotals(score, playerNumber);
    }

    private void updateLowerTotals(int score, int playerNumber) {
        JTextField grandTotal = scoreBoardMap.get("P" + playerNumber + "Grand total");
        if (grandTotal.getText().equals("")) {
            grandTotal.setText("0");
        }
        int currentTotal = Integer.parseInt(grandTotal.getText()) + score;
        grandTotal.setText(String.valueOf(currentTotal));

    }

    private void updateTotals(String choice, int score, int playerNumber) {
        int i;
        for (i = 0; i < LABELS.length; i++) {
            if (choice.equals(LABELS[i])) {
                break;
            }
        }
        if (i < 6) {
            updateUpperTotals(score, playerNumber);
        } else if (i < 12) {
            updateLowerTotals(score, playerNumber);
        }

    }

    private void updateScoreBoard(String choice, int score, int playerNumber) {
        if (playerNumber == positionInGame) {
            playerNumber = 1;

        } else {
            if (playerNumber < positionInGame) {
                playerNumber = positionInGame;
            }
        }

        JTextField textFieldToUpdate = scoreBoardMap.get("P" + playerNumber + choice);
        textFieldToUpdate.setText(String.valueOf(score));
        scoreBoardMap.put("P" + playerNumber + choice, textFieldToUpdate);
        updateTotals(choice, score, playerNumber);
    }

    private void uncheckDices() {
        jCheckBoxDiceResult1.setSelected(false);
        jCheckBoxDiceResult2.setSelected(false);
        jCheckBoxDiceResult3.setSelected(false);
        jCheckBoxDiceResult4.setSelected(false);
        jCheckBoxDiceResult5.setSelected(false);
    }

    private void saveDices() {
        calculatedScores.clear();
        dices.clear();
        putDicesInArray();
        comboBoxSaveOptions.removeAllItems();
        checkSingels();
        checkCombinations();
        int buttonPressed = JOptionPane.showConfirmDialog(null, saveGamePanel(), "Save Dices"
                , JOptionPane.OK_CANCEL_OPTION
                , JOptionPane.PLAIN_MESSAGE);
        if (buttonPressed == JOptionPane.OK_OPTION) {
            String choice = Objects.requireNonNull(comboBoxSaveOptions.getSelectedItem()).toString();
            int score = calculatedScores.get(choice);
            updateScoreBoard(choice, score, positionInGame);
            buttonSaveResult.setEnabled(false);
            buttonRollDices.setEnabled(false);
            displayNewDices(new String[]{"-", "-", "-", "-", "-"});
            sendMessage("turn_completed::" + choice + ";;" + score);
            uncheckDices();
        }
    }

    private void displayNewDices(String[] dices) {
        //Display dices in GUI
        if (!jCheckBoxDiceResult1.isSelected()) {
            jTextFieldDiceResult1.setText(dices[0]);
        }
        if (!jCheckBoxDiceResult2.isSelected()) {
            jTextFieldDiceResult2.setText(dices[1]);
        }
        if (!jCheckBoxDiceResult3.isSelected()) {
            jTextFieldDiceResult3.setText(dices[2]);
        }
        if (!jCheckBoxDiceResult4.isSelected()) {
            jTextFieldDiceResult4.setText(dices[3]);
        }
        if (!jCheckBoxDiceResult5.isSelected()) {
            jTextFieldDiceResult5.setText(dices[4]);
        }
    }

    //PANELS

    /**
     * creates the top panel and populates it with swing components
     *
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

        buttonRollDices.setEnabled(false);
        buttonRollDices.addActionListener(actionEvent -> rollDices());
        mainTopPanel.add(buttonRollDices);

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

        buttonSaveResult.setEnabled(false);
        buttonSaveResult.addActionListener(actionEvent -> saveDices());
        mainTopPanel.add(buttonSaveResult);

        return mainTopPanel;
    }

    /**
     * creates the right panel and populates it with swing components
     *
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
                sendMessage("chatt::" + player.getName() + ": " + jTextAreaChatInput.getText()));
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
     *
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
        jTextFieldLoginName.addAncestorListener(new RequestFocusListener());
        centerLoginPanel.add(jTextFieldLoginName);
        centerLoginPanel.add(jTextFieldLoginPassword);

        loginPanel.add(leftLoginPanel);
        loginPanel.add(centerLoginPanel);

        return loginPanel;
    }

    /**
     * creates the New user panel and populates it with swing components
     *
     * @return - returns the created JPanel
     */
    private JPanel newUserPanel() {
        JPanel newUserPanel = new JPanel();
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
        centerUserPanel.add(newUserInputPassword);

        newUserPanel.add(leftNewUserPanel);
        newUserPanel.add(centerUserPanel);


        return newUserPanel;
    }

    private JPanel invitePlayerPanel() {
        JPanel invitePanel = new JPanel();

        JPanel leftInvitePanel = new JPanel();
        leftInvitePanel.setLayout(new GridLayout(2, 2, 5, 5));
        leftInvitePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftInvitePanel.add(new JLabel("Invite players"));

        JPanel centerInvitePanel = new JPanel();
        centerInvitePanel.setLayout(new GridLayout(2, 2, 5, 5));
        centerInvitePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldInvitePlayers.addAncestorListener(new RequestFocusListener());
        centerInvitePanel.add(jTextFieldInvitePlayers);

        invitePanel.add(leftInvitePanel);
        invitePanel.add(centerInvitePanel);

        return invitePanel;
    }

    private JPanel joinGamePanel() {
        JPanel joinGamePanel = new JPanel();
        JPanel leftJoinGamePanel = new JPanel();
        leftJoinGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        leftJoinGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftJoinGamePanel.add(new JLabel("Join game: "));

        JPanel centerJoinGamePanel = new JPanel();
        centerJoinGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        centerJoinGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextFieldJoinGame.addAncestorListener(new RequestFocusListener());
        centerJoinGamePanel.add(jTextFieldJoinGame);

        joinGamePanel.add(leftJoinGamePanel);
        joinGamePanel.add(centerJoinGamePanel);

        return joinGamePanel;
    }

    private JPanel saveGamePanel() {
        JPanel saveGamePanel = new JPanel();
        JPanel leftSaveGamePanel = new JPanel();
        leftSaveGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        leftSaveGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftSaveGamePanel.add(new JLabel("Choose combination to save: "));

        JPanel centerSaveGamePanel = new JPanel();
        centerSaveGamePanel.setLayout(new GridLayout(2, 2, 5, 5));
        centerSaveGamePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        comboBoxSaveOptions.addAncestorListener(new RequestFocusListener());
        centerSaveGamePanel.add(comboBoxSaveOptions);

        saveGamePanel.add(leftSaveGamePanel);
        saveGamePanel.add(centerSaveGamePanel);

        return saveGamePanel;
    }

    private JPanel scoreBoardPanel() {


        //Create and populate the panel.
        JPanel scoreBoardPanel = new JPanel(new SpringLayout());

        JLabel h = new JLabel("Player: ", JLabel.CENTER);
        JLabel h1 = new JLabel("You", JLabel.CENTER);
        JLabel h2 = new JLabel("P 2", JLabel.CENTER);
        JLabel h3 = new JLabel("P 3", JLabel.CENTER);
        JLabel h4 = new JLabel("P 4", JLabel.CENTER);

        scoreBoardPanel.add(h);
        scoreBoardPanel.add(h1);
        scoreBoardPanel.add(h2);
        scoreBoardPanel.add(h3);
        scoreBoardPanel.add(h4);

        for (int i = 0; i < LABELS.length; i++) {
            JLabel l = new JLabel(LABELS[i] + ": ", JLabel.CENTER);
            scoreBoardPanel.add(l);

            JTextField textField = new JTextField();
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            textField.setBackground(Color.white);
            textField.setEditable(false);
            scoreBoardMap.put("P1" + LABELS[i], textField);

            JTextField textField1 = new JTextField();
            textField1.setHorizontalAlignment(SwingConstants.CENTER);
            textField1.setEditable(false);
            scoreBoardMap.put("P2" + LABELS[i], textField1);

            JTextField textField2 = new JTextField();
            textField2.setHorizontalAlignment(SwingConstants.CENTER);
            textField2.setEditable(false);
            scoreBoardMap.put("P3" + LABELS[i], textField2);

            JTextField textField3 = new JTextField();
            textField3.setHorizontalAlignment(SwingConstants.CENTER);
            textField3.setEditable(false);
            scoreBoardMap.put("P4" + LABELS[i], textField3);

            scoreBoardPanel.add(textField);
            scoreBoardPanel.add(textField1);
            scoreBoardPanel.add(textField2);
            scoreBoardPanel.add(textField3);
        }
        SpringUtilities.makeGrid(scoreBoardPanel, LABELS.length + 1, 5, 6, 6, 6, 6);

        return scoreBoardPanel;
    }

    /**
     * creates the menubar and populates it with swing components
     *
     * @return - returns the created menubar
     */
    private JMenuBar createMenuBar() {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Play");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);

        menuItemCreateNewPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_DOWN_MASK));
        menuItemCreateNewPlayer.addActionListener(actionEvent -> createNewUser());

        menuItemLogin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));
        menuItemLogin.addActionListener(actionEvent -> loginplayer());

        menuItemInvitePlayers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK));
        menuItemInvitePlayers.setEnabled(false);
        menuItemInvitePlayers.addActionListener(actionEvent -> invitePlayer());

        menuItemJoinGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.ALT_DOWN_MASK));
        menuItemJoinGame.setEnabled(false);
        menuItemJoinGame.addActionListener(actionEvent -> joinGame());

        menuItemScoreBoard.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)));
        menuItemScoreBoard.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)));
        menuItemExit.addActionListener(actionEvent -> System.exit(0));

        menu.add(menuItemCreateNewPlayer);
        menu.add(menuItemLogin);
        menu.add(menuItemInvitePlayers);
        menu.add(menuItemJoinGame);
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

        Yatzy mainPanel = new Yatzy(socketReader, socketWriter);

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
        frame.setSize(800, 720);
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
                    System.out.println("Received: " + incommingMessage);
                    String[] parts = incommingMessage.split("::");
                    String messageCode = parts[0];
                    String[] messageParts = parts[1].split(";;");
                    //GÖR OM TILL CASE/SWITCH
                    switch (messageCode) {
                        case "chatt":
                            //jTextAreaTextAreaChatArea.setText(jTextAreaTextAreaChatArea.getText() + "\n" + message);
                            jTextAreaTextAreaChatArea.setText(jTextAreaTextAreaChatArea.getText() + "\n" + messageParts[0]);
                            break;
                        case "new_user":
                            //String[] newUserParts = message.split(";;");
                            if (!messageParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(messageParts[0]));
                                player.setName(jTextFilednewUserInputName.getText());
                                player.setEmail(jTextFieldnewUserInputEmail.getText());
                            } else {
                                player.setID(-1);
                            }
                            break;
                        case "login_user":
                            //String[] loginUserParts = message.split(";;");
                            if (!messageParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(messageParts[0]));
                                player.setName(messageParts[1]);
                                player.setEmail(messageParts[2]);
                            } else {
                                player.setID(-1);
                            }
                            break;
                        case "invitations":
                            //String[] messageParts = message.split(";;");
                            positionInGame = Integer.parseInt(messageParts[0]);
                            JOptionPane.showMessageDialog(this, messageParts[1]);
                            buttonSendChat.setEnabled(true);
                            break;
                        case "player_added_to_game":
                            //String[] messageParts = message.split(";;");
                            positionInGame = Integer.parseInt(messageParts[0]);
                            buttonSendChat.setEnabled(true);
                            JOptionPane.showMessageDialog(this, messageParts[1]);
                            break;
                        case "game_started":
                            if (positionInGame != 1) {
                                JOptionPane.showMessageDialog(this, messageParts[0]);
                            } else {
                                playTurn();
                            }
                            break;
                        case "players_turn":
                            //String[] messageParts = message.split(";;");
                            int score = Integer.parseInt(messageParts[3]);
                            int playerNumber = Integer.parseInt(messageParts[1]);
                            if (messageParts[0].equals(String.valueOf(positionInGame))) {
                                playTurn();
                            }
                            updateScoreBoard(messageParts[2], score, playerNumber);

                            break;
                        case "rolled_dices":
                            //String[] rolledDicesParts = message.split(";");
                            dices.clear();
                            displayNewDices(messageParts);
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

package peter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Yatzy extends JPanel implements Runnable {

    public static final String[] LABELS = {"Aeces", "Twos", "Upper score", "Upper Bonus", "Upper total",
            "Chance", "YATZY",
            "Grand total"};

//    public static final String[] LABELS = {"Aeces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper score", "Upper Bonus", "Upper total",
//            "1 pair", "2 pairs", "3 of a kind", "4 of a kind", "Full house", "Sm Straight", "Lg Straight", "Chance", "YATZY",
//            "Grand total"};

    private ArrayList<String> unUsedScoreFields = new ArrayList<>();

    private static final int MAX_NUMBER_OF_DICE_ROLLS = 3;
    private final static String DEFAULTHOST = "127.0.0.1";
    private final static int DEFAULTPORT = 2000;

    private int numberOfDicesRolled = 1;
    private ArrayList<Integer> dices = new ArrayList<>();
    private Map<String, Integer> calculatedScores = new HashMap<String, Integer>();

    private Player player = new Player();
    private Game game = new Game();

    private MainPanel mainPanel = new MainPanel();
    private MainRightPanel mainRightPanel = new MainRightPanel();
    private ScoreBoard scoreboard = new ScoreBoard();

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;

    private static JMenuBar menuBar = new JMenuBar();

    /**
     * Create GUI components for the application
     */

    //Menu items
    private JMenuItem menuItemCreateNewPlayer = new JMenuItem("Create new player");
    private JMenuItem menuItemInvitePlayers = new JMenuItem("Invite players");
    private JMenuItem menuItemLogin = new JMenuItem("Login");
    private JMenuItem menuItemJoinGame = new JMenuItem("Join game");
    //private JMenuItem menuItemScoreBoard = new JMenuItem("My Scoreboard");
    private JMenuItem menuItemExit = new JMenuItem("Exit");

    //Textfields and checkboxes

    private JTextField jTextFilednewUserInputName = new JTextField(45);
    private JTextField jTextFieldnewUserInputEmail = new JTextField(45);
    private JTextField newUserInputPassword = new JPasswordField(45);

    private JTextField jTextFieldLoginName = new JTextField(45);
    private JTextField jTextFieldLoginPassword = new JPasswordField(45);

    private JTextField jTextFieldInvitePlayers = new JTextField(45);

    private JTextField jTextFieldJoinGame = new JTextField(15);

    //Dropdowns
    private JComboBox<String> comboBoxSaveOptions = new JComboBox<>(new String[]{});
    private JComboBox<String> comboBoxStikeOutOptions = new JComboBox<>(new String[]{});

    public Yatzy(BufferedReader socketReader, PrintWriter socketWriter) {

        this.socketReader = socketReader;
        this.socketWriter = socketWriter;

        //Create Menu
        menuBar = createMenuBar();
        //Set Layout for the frame
        setLayout(new BorderLayout());

        //Set actionlistners on buttons
        mainPanel.buttonRollDices.addActionListener(actionEvent -> rollDices());
        mainPanel.buttonSaveResult.addActionListener(actionEvent -> saveDices());
        mainRightPanel.buttonSendChat.addActionListener(actionEvent ->
                sendMessage("chatt::" + player.getName() + ": " + mainRightPanel.getNewChatMessage()));

        //Add panels to the frame
        add(mainPanel, BorderLayout.NORTH);
        add(mainRightPanel, BorderLayout.EAST);
        add(scoreboard, BorderLayout.CENTER);
    }

    /**
     * Enables/disabels swing components
     *
     * @param value - true/false
     */
    private void enableOptions(boolean value) {
        //menuItemScoreBoard.setEnabled(value);
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
                //setEnabled(false);

                sendMessage("login::" + jTextFieldLoginName.getText() + ";;" + jTextFieldLoginPassword.getText());
                while (player.getID() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (player.getID() == -1) {
                    JOptionPane.showMessageDialog(this, "Unable to Login! Either User don't exist, wrong username or wrong password entered!");
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

    private void resetScoreBoard(boolean clearScoreBoard) {
        //Clear and Fill unUsedScoreFieldsArray
        unUsedScoreFields.clear();
        unUsedScoreFields.addAll(Arrays.asList(LABELS));

        //Remove calculatedFields
        unUsedScoreFields.remove("Upper score");
        unUsedScoreFields.remove("Upper Bonus");
        unUsedScoreFields.remove("Upper total");
        unUsedScoreFields.remove("Grand total");

        //check if there is previous scores and if so clear scoreboard
        if (clearScoreBoard && game.isGameStarted()) {
            scoreboard.resetScoreBoard(game.getNumberOfPlayers());
        }
    }

    //Game play functions
    private void playTurn() {
        mainPanel.buttonRollDices.setEnabled(true);
        numberOfDicesRolled = 1;
        JOptionPane.showMessageDialog(this, "It it your turn! Please roll the dices to start the turn.");
    }

    private void rollDices() {
        if (numberOfDicesRolled <= MAX_NUMBER_OF_DICE_ROLLS) {
            sendMessage("roll_dices::na");
            mainPanel.buttonSaveResult.setEnabled(true);
            if (numberOfDicesRolled == MAX_NUMBER_OF_DICE_ROLLS) {
                mainPanel.buttonRollDices.setEnabled(false);
            }
            numberOfDicesRolled++;
        } else {
            mainPanel.buttonRollDices.setEnabled(false);
        }
    }

    private void putDicesInArray(String[] dicesString) {
        for(String dice : dicesString){
            dices.add(Integer.parseInt(dice));
        }
        System.out.println(dices);
        Collections.sort(dices);
        System.out.println(dices);
    }

    private void checkSingels() {
        for (int i = 1; i < 7; i++) {
            if (dices.contains(i)) {
                if (unUsedScoreFields.contains(LABELS[i - 1])) {
                    int sum = 0;
                    for (int dice : dices) {
                        if (dice == i) {
                            sum += i;
                        }
                    }
                    addCalculatedScore(LABELS[i - 1], sum);
                }
            }
        }
    }

    private void checkForMultiples(ArrayList<Integer> occurrencesOfDices) {
        int pair1Sum = 0;
        for (int i = 5; i >= 0; i--) {
            int occurrences = occurrencesOfDices.get(i);
            if (pair1Sum == 0 && occurrences > 1) {
                pair1Sum = (i + 1) * 2;
                if (unUsedScoreFields.contains("1 pair")) {
                    addCalculatedScore("1 pair", pair1Sum);
                }
            }
            if (pair1Sum != 0 && unUsedScoreFields.contains("2 pairs") && occurrences > 1) {
                if (!((i + 1 == (pair1Sum / 2)) && !(occurrences > 3))) {
                    addCalculatedScore("2 pairs", ((i + 1) * 2) + pair1Sum);
                }
            }
            if (unUsedScoreFields.contains("3 of a kind") && occurrences > 2) {
                addCalculatedScore("3 of a kind", (i + 1) * 3);
            }
            if (unUsedScoreFields.contains("4 of a kind") && occurrences > 3) {
                addCalculatedScore("4 of a kind", (i + 1) * 4);
            }
            if (unUsedScoreFields.contains("YATZY") && occurrences > 4) {
                addCalculatedScore("YATZY", 50);
            }
        }
    }

    private void checkCombinations() {
        ArrayList<Integer> occurrencesOfDices = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            occurrencesOfDices.add(Collections.frequency(dices, i));
        }
        checkForMultiples(occurrencesOfDices);
        checkForCombinations(occurrencesOfDices);
    }

    private void checkForCombinations(ArrayList<Integer> occurrencesOfDices) {
        //Check Full house
        ArrayList<Integer> sortedOccurences = new ArrayList<>(occurrencesOfDices);
        Collections.sort(sortedOccurences);
        if (unUsedScoreFields.contains("Full house") && sortedOccurences.get(5) == 3 && sortedOccurences.get(4) == 2) {
            int tripletIndex = occurrencesOfDices.indexOf(3);
            int pairIndex = occurrencesOfDices.indexOf(2);
            if (pairIndex != -1) {
                int sum = ((tripletIndex + 1) * 3) + ((pairIndex + 1) * 2);
                addCalculatedScore("Full house", sum);
            }
        }
        //Check sm straight
        if (unUsedScoreFields.contains("Sm Straight") && occurrencesOfDices.get(0) == 1 &&
                occurrencesOfDices.get(1) == 1 &&
                occurrencesOfDices.get(2) == 1 &&
                occurrencesOfDices.get(3) == 1 &&
                occurrencesOfDices.get(4) == 1) {
            addCalculatedScore("Sm Straight", 15);
        }

        //Check lg straight
        if (unUsedScoreFields.contains("Lg Straight") && occurrencesOfDices.get(1) == 1 &&
                occurrencesOfDices.get(2) == 1 &&
                occurrencesOfDices.get(3) == 1 &&
                occurrencesOfDices.get(4) == 1 &&
                occurrencesOfDices.get(5) == 1) {
            addCalculatedScore("Lg Straight", 20);
        }
        //add Chance
        if (unUsedScoreFields.contains("Chance")) {
            int chanceSum = 0;
            for (Integer d : dices) {
                chanceSum += d;
            }
            addCalculatedScore("Chance", chanceSum);
        }
    }

    private void addCalculatedScore(String scoreField, int score) {
        comboBoxSaveOptions.addItem(scoreField);
        calculatedScores.put(scoreField, score);
    }

    private void updateUnusedScoreFields(String scoreFileld) {
        unUsedScoreFields.remove(scoreFileld);
    }

    private void updateScoreBoard(String choice, int score, int playerNumber) {
        if (playerNumber == game.getPositionInGame()) {
            playerNumber = 1;

        } else {
            if (playerNumber < game.getPositionInGame()) {
                playerNumber = game.getPositionInGame();
            }
        }

        scoreboard.updateScoreBoard(playerNumber, choice, score);
        if (playerNumber == 1) {
            updateUnusedScoreFields(choice);
        }
        if (unUsedScoreFields.size() < 1) {
            sendMessage("player_completed_game::na");
            game.increasePlayersCompletedGame();
        }
    }

    private void showWinner() {
        int winner = 1;
        int highestScore = 0;
        String winnerText = "";

        ArrayList<Integer> finalResult = new ArrayList<>();
        switch (game.getNumberOfPlayers()) {
            case 4:
                finalResult.add(scoreboard.getScoreFieldValue("P4Grand total"));
            case 3:
                finalResult.add(scoreboard.getScoreFieldValue("P3Grand total"));
            case 2:
                finalResult.add(scoreboard.getScoreFieldValue("P2Grand total"));
                finalResult.add(scoreboard.getScoreFieldValue("P1Grand total"));
        }
        Collections.reverse(finalResult);
        for (int i = 0; i < finalResult.size(); i++) {
            if (finalResult.get(i) > highestScore) {
                highestScore = finalResult.get(i);
                winner = i + 1;
            }
        }

        if (winner == 1) {
            winnerText = "YOU";
        } else {
            winnerText = "player " + winner;
        }
        JOptionPane.showMessageDialog(this, "The winner is " + winnerText + " with a score of " + highestScore + "!");
    }

    private void saveScore(boolean strikeOut) {
        int score;
        String choice;

        if (strikeOut) {
            choice = Objects.requireNonNull(comboBoxStikeOutOptions.getSelectedItem()).toString();
            score = 0;
        } else {
            choice = Objects.requireNonNull(comboBoxSaveOptions.getSelectedItem()).toString();
            score = calculatedScores.get(choice);
        }

        sendMessage("turn_completed::" + choice + ";;" + score);
        updateScoreBoard(choice, score, game.getPositionInGame());
        dices.clear();
        mainPanel.buttonSaveResult.setEnabled(false);
        mainPanel.buttonRollDices.setEnabled(false);
        mainPanel.uncheckDices();
        mainPanel.displayNewDices(new String[]{"-", "-", "-", "-", "-"});
    }

    private void saveDices() {
        //prepare enviroment
        calculatedScores.clear();
        comboBoxSaveOptions.removeAllItems();
        comboBoxStikeOutOptions.removeAllItems();

        checkSingels();
        checkCombinations();

        for (String s : unUsedScoreFields) {
            if (!calculatedScores.containsKey(s)) {
                comboBoxStikeOutOptions.addItem(s);
            }
        }
        Object[] options = {"Save score",
                "Strike out",
                "Cancel"};
        int buttonpressed = JOptionPane.showOptionDialog(null, saveResultPanel(), "Save score", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

        switch (buttonpressed) {
            case JOptionPane.YES_OPTION:
                saveScore(false);
                break;
            case JOptionPane.NO_OPTION:
                saveScore(true);
                break;
        }
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

    private JPanel saveResultPanel() {
        JPanel saveResultPanel = new JPanel();
        JPanel leftSaveResultPanel = new JPanel();
        leftSaveResultPanel.setLayout(new GridLayout(3, 2, 5, 5));
        leftSaveResultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftSaveResultPanel.add(new JLabel("Choose combination to save: "));
        leftSaveResultPanel.add(new JLabel("If no, choose combination to strike out ( 0 points)"));

        JPanel centerSaveResultPanel = new JPanel();
        centerSaveResultPanel.setLayout(new GridLayout(3, 2, 5, 5));
        centerSaveResultPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        comboBoxSaveOptions.addAncestorListener(new RequestFocusListener());
        centerSaveResultPanel.add(comboBoxSaveOptions);
        centerSaveResultPanel.add(comboBoxStikeOutOptions);

        saveResultPanel.add(leftSaveResultPanel);
        saveResultPanel.add(centerSaveResultPanel);

        return saveResultPanel;
    }

    /**
     * creates the menubar and populates it with swing components
     *
     * @return - returns the created menubar
     */
    private JMenuBar createMenuBar() {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
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

//        menuItemScoreBoard.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)));
//        menuItemScoreBoard.setEnabled(false);

        menuItemExit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK)));
        menuItemExit.addActionListener(actionEvent -> System.exit(0));

        menu.add(menuItemCreateNewPlayer);
        menu.add(menuItemLogin);
        menu.add(menuItemInvitePlayers);
        menu.add(menuItemJoinGame);
        //menu.add(menuItemScoreBoard);
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
        JFrame frame = new JFrame("Peters online Yatzy game!");
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
                            String sender[] = messageParts[0].split(":");
                            if (sender[0].equals(player.getName())) {
                                mainRightPanel.appendNewChattMessage("\n" + messageParts[0], Color.red);
                            } else {
                                mainRightPanel.appendNewChattMessage("\n" + messageParts[0], Color.black);
                            }

                            break;
                        case "new_user":
                            if (!messageParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(messageParts[0]));
                                player.setName(jTextFilednewUserInputName.getText());
                                player.setEmail(jTextFieldnewUserInputEmail.getText());
                            } else {
                                player.setID(-1);
                            }
                            break;
                        case "login_user":
                            if (!messageParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(messageParts[0]));
                                player.setName(messageParts[1]);
                                player.setEmail(messageParts[2]);
                            } else {
                                player.setID(-1);
                            }
                            break;
                        case "invitations":
                            game.setPositionInGame(Integer.parseInt(messageParts[0]));
                            game.setNumberOfPlayers(Integer.parseInt(messageParts[1]));
                            JOptionPane.showMessageDialog(this, messageParts[2]);
                            mainRightPanel.buttonSendChat.setEnabled(true);
                            resetScoreBoard(true);
                            break;
                        case "player_added_to_game":
                            game.setPositionInGame(Integer.parseInt(messageParts[0]));
                            game.setNumberOfPlayers(Integer.parseInt(messageParts[1]));
                            if (game.getPositionInGame() != -1) {
                                mainRightPanel.buttonSendChat.setEnabled(true);
                                game.setGameStarted(true);
                                resetScoreBoard(true);
                            }
                            JOptionPane.showMessageDialog(this, messageParts[2]);
                            break;
                        case "game_started":
                            if (game.getPositionInGame() != 1) {
                                JOptionPane.showMessageDialog(this, messageParts[0]);
                            } else {
                                playTurn();
                            }
                            break;
                        case "players_turn":
                            int score = Integer.parseInt(messageParts[3]);
                            int playerNumber = Integer.parseInt(messageParts[1]);
                            if (messageParts[0].equals(String.valueOf(game.getPositionInGame())) && unUsedScoreFields.size() > 0) {
                                playTurn();
                            }
                            updateScoreBoard(messageParts[2], score, playerNumber);
                            break;
                        case "rolled_dices":
                            dices.clear();
                            mainPanel.displayNewDices(messageParts);
                            putDicesInArray(messageParts);
                            break;
                        case "player_completed_game":
                            game.increasePlayersCompletedGame();
                            break;
                        case "game_completed":
                            showWinner();
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

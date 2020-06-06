/**
 * The Yatzy program implements an application that
 * shows a GUI for the player to play a game of yatzy
 * with other players on the internet
 *
 * @author Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since 2020-06-04
 */

package peter;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Yatzy extends JPanel implements Runnable {

//    public static final String[] LABELS = {"Aeces", "Twos", "Upper score", "Upper Bonus", "Upper total",
//            "Chance", "YATZY",
//            "Grand total"};

    public static final String[] LABELS = {"Aeces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper score", "Upper Bonus", "Upper total",
            "1 pair", "2 pairs", "3 of a kind", "4 of a kind", "Full house", "Sm Straight", "Lg Straight", "Chance", "YATZY",
            "Grand total"};

    private ArrayList<String> unUsedScoreFields = new ArrayList<>();

    private static final int MAX_NUMBER_OF_DICE_ROLLS = 3;
    private final static String DEFAULTHOST = "127.0.0.1";
    //private final static String DEFAULTHOST = "213.80.102.136";
    private final static int DEFAULTPORT = 2000;

    private int numberOfDicesRolled = 1;
    private ArrayList<Integer> dices = new ArrayList<>();
    private Map<String, Integer> calculatedScores = new HashMap<String, Integer>();

    private Player player = new Player();
    private Game game = new Game();

    private XmlDocumentHandler xmlDocumentHandler = new XmlDocumentHandler();

    private static MenuBar menuBar = new MenuBar();

    private MainPanel mainPanel = new MainPanel();
    private MainRightPanel mainRightPanel = new MainRightPanel();
    private ScoreBoard scoreboard = new ScoreBoard();

    private SaveResultPanel saveResultPanel = new SaveResultPanel();

    private final BufferedReader socketReader;
    private final PrintWriter socketWriter;

    /**
     * The constructor generates a new game GUI
     *
     * @param socketReader - socketReader for incoming messages
     * @param socketWriter - socketWriter for outgoing messages
     */
    public Yatzy(BufferedReader socketReader, PrintWriter socketWriter) {

        this.socketReader = socketReader;
        this.socketWriter = socketWriter;

        //Set Layout for the frame
        setLayout(new BorderLayout());

        //Set actionListners to menu items
        menuBar.menuItemCreateNewPlayer.addActionListener(actionEvent -> createNewUser());
        menuBar.menuItemLogin.addActionListener(actionEvent -> loginplayer());
        menuBar.menuItemInvitePlayers.addActionListener(actionEvent -> invitePlayer());
        menuBar.menuItemJoinGame.addActionListener(actionEvent -> joinGame());
        menuBar.menuItemExit.addActionListener(actionEvent -> System.exit(0));

        //Set actionlistners on buttons
        mainPanel.buttonRollDices.addActionListener(actionEvent -> rollDices());
        mainPanel.buttonSaveResult.addActionListener(actionEvent -> saveDices());
        mainRightPanel.buttonSendChat.addActionListener(actionEvent -> {
            sendMessage("chatt", player.getName() + ": " + mainRightPanel.getNewChatMessage());
            mainRightPanel.clearChatInput();
        });

        //Add panels to the frame
        add(mainPanel, BorderLayout.NORTH);
        add(mainRightPanel, BorderLayout.EAST);
        add(scoreboard, BorderLayout.CENTER);
    }

    /**
     * Enables/disabels swing components in the menubar
     *
     * @param value - true/false
     */
    private void enableMenuOptions(boolean value) {
        menuBar.enabelMenuOptions(value);
    }

    /**
     * Login player on the server, shows a JOptionPane for signing in
     */
    private void loginplayer() {
        LoginPanel loginPanel = new LoginPanel();
        boolean endLogin = false;
        while (!endLogin) {
            //Display login windows and wait for a response
            int buttonPressed = JOptionPane.showConfirmDialog(null, loginPanel, "User Login"
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            //Check if user pushed OK button
            if (buttonPressed == JOptionPane.OK_OPTION) {
                player.setID(0);
                player.setName("");
                player.setEmail("");
                //Sends login request to server
                sendMessage("login", loginPanel.getLoginName() + ";;" + loginPanel.getLoginPassword());
                //Wait for response from server by checking if player ID have been updated
                while (player.getID() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //If unable to login
                if (player.getID() == -1) {
                    JOptionPane.showMessageDialog(this, "Unable to Login! Either User don't exist, wrong username or wrong password entered!");
                } else {
                    //On successful login update player info
                    loginPanel.setLoginPassword("");
                    enableMenuOptions(true);
                    endLogin = true;
                    JOptionPane.showMessageDialog(this, "Welcom back " + player.getName() + "!");
                }
            } else {
                //if cancel button pressed end login process
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
        NewUserPanel newUserPanel = new NewUserPanel();
        boolean endUserInput = false;
        while (!endUserInput) {
            //Display create new user windows and wait for a response
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, newUserPanel, "New user Form : "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            //Check if OK button is pressed
            if (buttonPressed == JOptionPane.OK_OPTION) {
                player.setName(newUserPanel.getNewUserName());
                player.setEmail(newUserPanel.getNewUserEmail());
                sendMessage("new_user", player.getName() + ";;" + player.getEmail() + ";;" + newUserPanel.getNewUserPassword());
                //Wait for server response
                while (player.getID() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //Check if new user was created -1 is unsuccessful
                if (player.getID() == -1) {
                    JOptionPane.showMessageDialog(this, "Unable to create New user!");
                } else if (player.getID() == -2) {
                    JOptionPane.showMessageDialog(this, "User already exists! Use another email account");
                } else {
                    enableMenuOptions(true);
                    JOptionPane.showMessageDialog(this, "New user " + player.getName() + " successfully created!");
                }
            }
            //end new user process
            endUserInput = true;
            newUserPanel.setNewUserPassword("");
        }
    }

    /**
     * Send message to the game server
     *
     * @param message - message to be sent
     */
    private void sendMessage(String code, String message) {
        String xmlString = xmlDocumentHandler.createXmlString(code, player, message, game);
        socketWriter.println(xmlString);
    }

    /**
     * Invite players to the game, players are separated with ;
     */
    private void invitePlayer() {
        InvitePlayerPanel invitePlayerPanel = new InvitePlayerPanel();
        boolean endInviteInput = false;
        while (!endInviteInput) {
            //Display a invite player window
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, invitePlayerPanel, "Invite players: "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            //Check if OK button is pressed
            if (buttonPressed == JOptionPane.OK_OPTION) {
                //Check the number of players invited is more than 4
                String[] invitedPlayers = invitePlayerPanel.getInviteedPlayers().split(";");
                if(invitedPlayers.length < 5) {
                    //Send invitation request to server
                    sendMessage("invite_players", player.getEmail() + ";;" + player.getID() + ";;" + invitePlayerPanel.getInviteedPlayers());
                    endInviteInput = true;
                }else{
                    JOptionPane.showMessageDialog(this,"Maximum players per game is 4, please remove players so there is a maximum of 4 players to invite.");
                }
            }else{
                endInviteInput = true;
            }
        }
    }

    /**
     * Join game, lets the player Join a game by supplying gameID received by email
     */
    private void joinGame() {
        JoinGamePanel joinGamePanel = new JoinGamePanel();
        boolean endJoinGame = false;
        while (!endJoinGame) {
            //Display join game window
            int buttonPressed = JOptionPane.showConfirmDialog(
                    null, joinGamePanel, "Join Game: "
                    , JOptionPane.OK_CANCEL_OPTION
                    , JOptionPane.PLAIN_MESSAGE);
            //check if OK button is pressed
            if (buttonPressed == JOptionPane.OK_OPTION) {
                //Send join game request to server
                sendMessage("join_game", player.getID() + ";;" + player.getEmail() + ";;" + joinGamePanel.getJoinGame());
            }
            endJoinGame = true;
        }

    }

    /**
     * Resets the scoreboard, removes all previous scores and players
     */
    private void resetScoreBoard() {
        //Clear and Fill unUsedScoreFieldsArray
        unUsedScoreFields.clear();
        unUsedScoreFields.addAll(Arrays.asList(LABELS));

        //Remove calculatedFields
        unUsedScoreFields.remove("Upper score");
        unUsedScoreFields.remove("Upper Bonus");
        unUsedScoreFields.remove("Upper total");
        unUsedScoreFields.remove("Grand total");

        //check if there is previous scores and if so clear scoreboard
        if (game.isGameStarted()) {
            //Request scoreboard to be reset
            scoreboard.resetScoreBoard(game.getNumberOfPlayers());
        }
    }

    //Gameplay functions

    /**
     * playturn, informs the player that is the players turn and enables Roll dice button
     */
    private void playTurn() {
        mainPanel.buttonRollDices.setEnabled(true);
        numberOfDicesRolled = 1;
        JOptionPane.showMessageDialog(this, "It it your turn! Please roll the dices to start the turn.");
    }

    /**
     * RollDice, requests new dices from server, enables the save result button and increase the number of dice rolls performed this turn
     */
    private void rollDices() {
        //Check if there is remaining dice rolls left this turn
        if (numberOfDicesRolled <= MAX_NUMBER_OF_DICE_ROLLS) {
            sendMessage("roll_dices", "na");
            mainPanel.buttonSaveResult.setEnabled(true);
            if (numberOfDicesRolled == MAX_NUMBER_OF_DICE_ROLLS) {
                mainPanel.buttonRollDices.setEnabled(false);
            }
            //increase number of dices rolled this turn
            numberOfDicesRolled++;
        } else {
            //disable roll dice button if maximum number of dice rolls is performed
            mainPanel.buttonRollDices.setEnabled(false);
        }
    }

    /**
     * Put the recived dices in the dices arraylist
     *
     * @param dicesString - dices received from server
     */
    private void putDicesInArray(String[] dicesString) {
        for (String dice : dicesString) {
            dices.add(Integer.parseInt(dice));
        }
        //sort the dices in the arraylist
        Collections.sort(dices);
    }

    /**
     * Check if which singles is possible for the player, put possible singles in dropdown for saving score
     */
    private void checkSingles() {
        //Check for singles in the dices arraylist
        for (int i = 1; i < 7; i++) {
            if (dices.contains(i)) {
                //if the single is unsaved add it to save dropdown
                if (unUsedScoreFields.contains(LABELS[i - 1])) {
                    //calculate the score for the single
                    int sum = 0;
                    for (int dice : dices) {
                        if (dice == i) {
                            sum += i;
                        }
                    }
                    //adds the score to add calculated score structure
                    addCalculatedScore(LABELS[i - 1], sum);
                }
            }
        }
    }

    /**
     * Check for multiples (pair, 2 pair, Trips, quadruples and Yatzy)
     *
     * @param occurrencesOfDices - ArrayList with number of dices that are the same, all possible is represented in the arraylist
     */
    private void checkForMultiples(ArrayList<Integer> occurrencesOfDices) {
        int pair1Sum = 0;
        //loop through the different dice values staring with the highest number
        for (int i = 5; i >= 0; i--) {
            int occurrences = occurrencesOfDices.get(i);
            //check id pair is already identified and if that the dice value is a pair
            if (pair1Sum == 0 && occurrences > 1) {
                //Check id 1 pair is unused
                if (unUsedScoreFields.contains("1 pair")) {
                    //Calculate score
                    pair1Sum = (i + 1) * 2;
                    //add the score to calculated Score structure
                    addCalculatedScore("1 pair", pair1Sum);
                }
            }
            //check that a pair is previously identified, that 2 pair is unused and if that the dice value is a pair
            if (pair1Sum != 0 && unUsedScoreFields.contains("2 pairs") && occurrences > 1) {
                //check if it is possible to make 2 pair from the same dice value
                if (!((i + 1 == (pair1Sum / 2)) && !(occurrences > 3))) {
                    //add and calculate the score to calculated Score structure
                    addCalculatedScore("2 pairs", ((i + 1) * 2) + pair1Sum);
                }
            }
            //Check if there is any trips in dices
            if (unUsedScoreFields.contains("3 of a kind") && occurrences > 2) {
                //add and calculate the score to calculated Score structure
                addCalculatedScore("3 of a kind", (i + 1) * 3);
            }
            //Check if there is any quads in dices
            if (unUsedScoreFields.contains("4 of a kind") && occurrences > 3) {
                //add and calculate the score to calculated Score structure
                addCalculatedScore("4 of a kind", (i + 1) * 4);
            }
            //check if is Yatzy in the dices
            if (unUsedScoreFields.contains("YATZY") && occurrences > 4) {
                //add and calculate the score to calculated Score structure
                addCalculatedScore("YATZY", 50);
            }
        }
    }

    /**
     * Check for combos and multiples in dices
     */
    private void checkCombinations() {
        ArrayList<Integer> occurrencesOfDices = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            occurrencesOfDices.add(Collections.frequency(dices, i));
        }
        checkForMultiples(occurrencesOfDices);
        checkForCombinations(occurrencesOfDices);
    }

    /**
     * Check if there is any combos in dices
     *
     * @param occurrencesOfDices - ArrayList with number of dices that are the same, all possible is represented in the arraylist
     */
    private void checkForCombinations(ArrayList<Integer> occurrencesOfDices) {
        //Check for Full house
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
        //Check for sm straight
        if (unUsedScoreFields.contains("Sm Straight") && occurrencesOfDices.get(0) == 1 &&
                occurrencesOfDices.get(1) == 1 &&
                occurrencesOfDices.get(2) == 1 &&
                occurrencesOfDices.get(3) == 1 &&
                occurrencesOfDices.get(4) == 1) {
            //add the score to calculated Score structure
            addCalculatedScore("Sm Straight", 15);
        }

        //Check for lg straight
        if (unUsedScoreFields.contains("Lg Straight") && occurrencesOfDices.get(1) == 1 &&
                occurrencesOfDices.get(2) == 1 &&
                occurrencesOfDices.get(3) == 1 &&
                occurrencesOfDices.get(4) == 1 &&
                occurrencesOfDices.get(5) == 1) {
            //add the score to calculated Score structure
            addCalculatedScore("Lg Straight", 20);
        }
        if (unUsedScoreFields.contains("Chance")) {
            int chanceSum = 0;
            for (Integer d : dices) {
                chanceSum += d;
            }
            //add the score to calculated Score structure
            addCalculatedScore("Chance", chanceSum);
        }
    }

    /**
     * Add scores to calculated score structure
     *
     * @param scoreField - Scorefiled for which the score is added
     * @param score      - score to add
     */
    private void addCalculatedScore(String scoreField, int score) {
        saveResultPanel.addToSaveOptions(scoreField);
        calculatedScores.put(scoreField, score);
    }

    /**
     * removes the saved scoreField from unusedScoreField
     *
     * @param scoreFileld
     */
    private void updateUnusedScoreFields(String scoreFileld) {
        unUsedScoreFields.remove(scoreFileld);
    }

    /**
     * Update the scorebord
     *
     * @param scoreField   - which scorefield to update in the Scoreboard
     * @param score        - Which score to add to the scoreboard
     * @param playerNumber - for which player to update the score
     */
    private void updateScoreBoard(String scoreField, int score, int playerNumber) {
        //Check if the update is for your self
        if (playerNumber == game.getPositionInGame()) {
            playerNumber = 1;

        } else {
            //corrects the player "column" to update if the player is the first player in the game
            if (playerNumber < game.getPositionInGame()) {
                //playerNumber = game.getPositionInGame();
                playerNumber++;
            }
        }

        //Update the scoreBoard
        scoreboard.updateScoreBoard(playerNumber, scoreField, score);
        //Check if it the players own scoreBoard to update the remove saved score from unused scorefields
        if (playerNumber == 1) {
            updateUnusedScoreFields(scoreField);
        }
        //Check if all scores are used
        if (unUsedScoreFields.size() < 1) {
            sendMessage("player_completed_game", "na");
            game.increasePlayersCompletedGame();
        }
    }

    /**
     * Calculate and show the winner
     */
    private void showWinner() {
        int winner = 1;
        int highestScore = 0;
        String winnerText = "";

        ArrayList<Integer> finalResult = new ArrayList<>();
        //Check which fields to check for winner
        switch (game.getNumberOfPlayers()) {
            case 4:
                finalResult.add(scoreboard.getScoreFieldValue("P4Grand total"));
            case 3:
                finalResult.add(scoreboard.getScoreFieldValue("P3Grand total"));
            case 2:
                finalResult.add(scoreboard.getScoreFieldValue("P2Grand total"));
                finalResult.add(scoreboard.getScoreFieldValue("P1Grand total"));
        }
        //Check for which player is the winner
        Collections.reverse(finalResult);
        for (int i = 0; i < finalResult.size(); i++) {
            if (finalResult.get(i) > highestScore) {
                highestScore = finalResult.get(i);
                winner = i + 1;
            }
        }

        //Display the winner
        if (winner == 1) {
            winnerText = "YOU";
        } else {
            winnerText = game.getPlayerName(winner - 1);
        }
        JOptionPane.showMessageDialog(this, "The winner is " + winnerText + " with a score of " + highestScore + "!");
    }

    /**
     * save score, Show savescore window
     *
     * @param strikeOut - boolean to know if strike out or save score
     */
    private void saveScore(boolean strikeOut) {
        int score;
        String choice;

        //Check if strike out or to save score
        if (strikeOut) {
            choice = saveResultPanel.getSelectedStrikeOutOption();
            score = 0;
        } else {
            choice = saveResultPanel.getSelectedSaveOption();
            score = calculatedScores.get(choice);
        }

        //Notify server that the turn is completed
        sendMessage("turn_completed", choice + ";;" + score);

        //update scoreboard
        updateScoreBoard(choice, score, game.getPositionInGame());

        //clear diced and disable buttons
        dices.clear();
        mainPanel.buttonSaveResult.setEnabled(false);
        mainPanel.buttonRollDices.setEnabled(false);
        mainPanel.uncheckDices();
        mainPanel.displayNewDices(new String[]{"-", "-", "-", "-", "-"});
    }

    /**
     * Check possible scores and save the chosen one
     */
    private void saveDices() {
        //prepare enviroment
        calculatedScores.clear();
        saveResultPanel.clearAllSaveOptions();
        dices = mainPanel.getDicesFromBoard();

        //Check possible scores
        checkSingles();
        checkCombinations();

        //add not possible scores that are unused to strike out dropdown
        for (String s : unUsedScoreFields) {
            if (!calculatedScores.containsKey(s)) {
                saveResultPanel.addToStrikeOutOptions(s);
                //comboBoxStikeOutOptions.addItem(s);
            }
        }
        Object[] options = {"Save score",
                "Strike out",
                "Cancel"};
        int buttonpressed = JOptionPane.showOptionDialog(null, saveResultPanel, "Save score", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

        //Save score depending on button pressed
        switch (buttonpressed) {
            case JOptionPane.YES_OPTION:
                if (saveResultPanel.checkSaveOptions()) {
                    saveScore(false);
                }
                break;
            case JOptionPane.NO_OPTION:
                saveScore(true);
                break;
        }
    }

    /**
     * Setup communication with the server, Starts the GUI
     */
    private static void createAndShowGUI() {
        //Creates connection to server
        Socket socket;
        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;
        try {
            socket = new Socket(DEFAULTHOST, DEFAULTPORT);
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to game server, please try again later");
            e.printStackTrace();
        }

        Yatzy yatzyGame = new Yatzy(socketReader, socketWriter);
        //create a Receiveer thread
        Thread receiveThread = new Thread(yatzyGame);
        receiveThread.start();

        //Display GUI
        JFrame frame = new JFrame("Peters online Yatzy game!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(yatzyGame);
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
            //keeps the receiver alive
            while (true) {
                if (socketReader.ready()) {
                    String incommingMessage = socketReader.readLine();
                    String messageCode = xmlDocumentHandler.parseXml(incommingMessage, "code");
                    String[] messageParts = xmlDocumentHandler.parseXml(incommingMessage, "body").split(";;");

                    //Choose action depending on code received
                    switch (messageCode) {
                        case "chatt":
                            String sender[] = messageParts[0].split(":");
                            if (sender[0].equals(player.getName())) {
                                mainRightPanel.appendNewChattMessage("\n" + messageParts[0], Color.red);
                            } else {
                                mainRightPanel.appendNewChattMessage("\n" + messageParts[0], Color.black);
                            }
                            break;
                        case "game_completed":
                            showWinner();
                            break;
                        case "game_started":
                            if (game.getPositionInGame() != 1) {
                                JOptionPane.showMessageDialog(this, messageParts[0]);
                            } else {
                                resetScoreBoard();
                                game.setGameStarted(true);
                                playTurn();
                            }
                            break;
                        case "invitations":
                            game.setPositionInGame(Integer.parseInt(messageParts[0]));
                            game.setNumberOfPlayers(Integer.parseInt(messageParts[1]));
                            JOptionPane.showMessageDialog(this, messageParts[2]);
                            mainRightPanel.buttonSendChat.setEnabled(true);
                            resetScoreBoard();
                            scoreboard.updatePlayersName(player.getName(), 1);
                            break;
                        case "login_user":
                            if (!messageParts[0].equals("-1")) {
                                player.setID(Integer.valueOf(messageParts[0]));
                                player.setName(messageParts[1]);
                                player.setEmail(messageParts[2]);
                                scoreboard.updatePlayersName(player.getName(), 1);
                            } else {
                                player.setID(-1);
                            }
                            break;

                        case "new_user":
                            String playerID = messageParts[0];
                            player.setID(Integer.valueOf(playerID));
                            break;
                        case "player_added_to_game":
                            game.addPlayerName(player.getName());
                            game.setPositionInGame(Integer.parseInt(messageParts[0]));
                            game.setNumberOfPlayers(Integer.parseInt(messageParts[1]));
                            if (game.getPositionInGame() != -1) {
                                mainRightPanel.buttonSendChat.setEnabled(true);
                                game.setGameStarted(true);
                                resetScoreBoard();
                            }
                            JOptionPane.showMessageDialog(this, messageParts[2]);
                            break;
                        case "player_completed_game":
                            game.increasePlayersCompletedGame();
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
                        case "update_player_names":
                            String[] updatePlayerNamesParts = messageParts[0].split(";");
                            String playerToUpdateName = updatePlayerNamesParts[0];
                            int playerIndex = Integer.parseInt(updatePlayerNamesParts[1]);
                            if(playerIndex != game.getPositionInGame()) {
                                if (playerIndex < game.getPositionInGame()) {
                                    playerIndex++;
                                }
                                scoreboard.updatePlayersName(playerToUpdateName, playerIndex);
                            }
                            game.addPlayerName(playerToUpdateName);
                            break;
                    }
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * The Yatzyserver program implements an application that
 * allows player to play a game of yatzy
 * with other players on the internet using the Yatzy client
 *
 * @author Peter Fröberg, pefr7147@student.su.se
 * @version 1.0
 * @since 2020-06-04
 */

package peter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class YatzyServer implements Runnable {

    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private Mailhandler mailhandler = new Mailhandler("smtp.gmail.com", "xx", "xx");

    private final static int DEFAULTPORT = 2000;
    private final static String DEFAULTDBSERVER = "xx";
    private final static String DEFAULTDBUSER = "xx";
    private final static String DEFAULTDBPASSWORD = "xx";

    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<LinkedBlockingQueue<String>>> gamesMessagingQueues = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<LinkedBlockingQueue<String>> gameMessagesQueues;

    private final static Object Lock = new Object();
    private final Socket clientSocket;


    private LinkedBlockingQueue<String> playerMessageQueue;
    private Game currentGame = new Game();
    private Player player;
    private String dbServer;
    private String dbUser;
    private String dbPassword;

    private XmlDocumentHandler xmlDocumentHandler = new XmlDocumentHandler();

    private YatzyServer(Socket clientSocket, LinkedBlockingQueue<String> playerMessageQueue, String[] args) {
        this.clientSocket = clientSocket;
        this.playerMessageQueue = playerMessageQueue;
        if(args.length == 3) {
            dbServer = args[0];
            dbUser = args[1];
            dbPassword = args[2];
        }else{
            dbServer = DEFAULTDBSERVER;
            dbUser = DEFAULTDBUSER;
            dbPassword = DEFAULTDBPASSWORD;
        }

    }

    /**
     * Create User, send a create request to the database handler and send a result of the create user request to the player
     *
     * @param message - Message including username, email and password for the user to create, each property separated with ;;
     */
    private void createNewUser(String message) {
        databaseHandler.connectToDatabase(dbServer, dbUser, dbPassword);
        String[] newUserParts = message.split(";;");
        String name = newUserParts[0];
        String email = newUserParts[1];
        String password = newUserParts[2];
        player = databaseHandler.CreateNewPlayer(name, email, password);
        sendToMyMessageQueue("new_user", String.valueOf(player.getID()));
        //databaseHandler.disconnectDatabase();
    }

    /**
     * Login player to the server, sends a request to verify email and password to the databasehandler and return a message to the
     * player trying to login
     *
     * @param message - message including email and password for the user trying to login, each property separated with ;;
     */
    private void loginPlayer(String message) {
        databaseHandler.connectToDatabase(dbServer, dbUser, dbPassword);
        String[] loginUserParts = message.split(";;");
        String username = loginUserParts[0];
        String password = loginUserParts[1];
        player = databaseHandler.login(username, password);
        //notifying the player of the login result
        if (player != null) {
            sendToMyMessageQueue("login_user", player.getID() + ";;" + player.getName() + ";;" + player.getEmail());
        } else {
            sendToMyMessageQueue("login_user", "-1");
        }
        //databaseHandler.disconnectDatabase();
    }

    /**
     * invite player to the game, sends a request to the databasehandler to create a game and add users
     *
     * @param message - message including player emails for the invited players, each property separated with ;
     */
    private void invitePlayers(String message) {
        databaseHandler.connectToDatabase(dbServer, dbUser, dbPassword);
        //extract info from message
        String[] invitedPlayersParts = message.split(";;");
        String[] players = invitedPlayersParts[2].split(";");
        String invitingPlayer = invitedPlayersParts[0];
        int playerID = Integer.valueOf(invitedPlayersParts[1]);
        currentGame.setNumberOfPlayers(players.length + 1);
        currentGame.setID(databaseHandler.invitePlayers(players));
        String body = "You are invited for a game of Yahtzee by " + invitingPlayer + "\n Start the yahtzee program and use the \"join game\" option and input the game# " + String.valueOf(currentGame.getID()) + " in the play menu to join";
        String result = "";
        //Sends a invitation email to the invited players
        for (String playerToInvite : players) {
            result = mailhandler.send(playerToInvite, "testarepostkurs@gmail.com", "Yahtzee invitation", body);
        }
        currentGame.setPositionInGame(1);
        //notify the inviting player of created game
        sendToMyMessageQueue("invitations", currentGame.getPositionInGame() + ";;" + currentGame.getNumberOfPlayers() + ";;" + result);
        //add inviting player to the new game
        databaseHandler.addPlayerToGame(currentGame.getID(), playerID);
        //Create communicationscahnnel for in game communication
        createCommnunicationForGame();
        //Request a thread to check if all players are connected to the game
        checkGameStarted();
    }

    /**
     * Join invited players to the game
     *
     * @param message - message with player ID, email of player, game to join, each property separated with ;;
     */
    private void joinPlayerToGame(String message) {
        databaseHandler.connectToDatabase(dbServer, dbUser, dbPassword);
        //extract info from message
        String[] joinGameParts = message.split(";;");
        int playerID = Integer.parseInt(joinGameParts[0]);
        String playerEmail = joinGameParts[1];
        currentGame.setID(Integer.parseInt(joinGameParts[2]));
        //Add player to the game
        String playerAdded = databaseHandler.joinGame(playerID, playerEmail, currentGame.getID());
        String[] playerAddedToGameParts = playerAdded.split(";;");
        //update game object with game details
        currentGame.setPositionInGame(Integer.parseInt(playerAddedToGameParts[1]));
        currentGame.setNumberOfPlayers(Integer.parseInt(playerAddedToGameParts[0]));
        String messageToPlayer = playerAddedToGameParts[2];
        //notify player
        sendToMyMessageQueue("player_added_to_game", currentGame.getPositionInGame() + ";;" + currentGame.getNumberOfPlayers() + ";;" + messageToPlayer);
        joinCommunicationForGame();
        //databaseHandler.disconnectDatabase();
    }

    /**
     * turn completed, handles when a player have completed its turn and send turn information to players
     *
     * @param message
     */
    private void turnCompleted(String message) {
        //extract info from message
        String[] turnCompetedParts = message.split(";;");
        String scoreField = turnCompetedParts[0];
        String score = turnCompetedParts[1];
        int nextPlayer = currentGame.getPositionInGame() + 1;
        if (nextPlayer > currentGame.getNumberOfPlayers()) {
            nextPlayer = 1;
        }
        //Notify players of next player to play and send score info to other players
        String newMessage = nextPlayer + ";;" + currentGame.getPositionInGame() + ";;" + scoreField + ";;" + score;
        sendToInGamePlayers("players_turn", newMessage, false);
    }

    /**
     * player Completed game, update game info about how many players that have completed the game, when all players completed game
     * the player is notified
     */
    private void playerCompletedGame() {
        currentGame.increasePlayersCompletedGame();
        //check if all players completed the game, and notify player
        if (currentGame.getNumberOfPlayers() - currentGame.getPlayersCompletedGame() == 0) {
            sendToInGamePlayers("game_completed", "na", true);
            databaseHandler.setGameState(currentGame.getID(), "Finished");
        } else {
            sendToInGamePlayers("player_completed_game", "na", false);
        }
    }

    /**
     * Create a new communication channel for ingame communications and adds the player to the channel
     */
    private void createCommnunicationForGame() {
        gameMessagesQueues = new CopyOnWriteArrayList<>();
        gameMessagesQueues.add(playerMessageQueue);
        gamesMessagingQueues.put(currentGame.getID(), gameMessagesQueues);
    }

    /**
     * add player to ingame communication channel
     */
    private void joinCommunicationForGame() {
        gameMessagesQueues = gamesMessagingQueues.get(currentGame.getID());
        gameMessagesQueues.add(playerMessageQueue);
    }

    /**
     * Checks if the game is started by requesting a check from the databasehandler if state is changed to started
     * and if started informs players that game is started. Only the inviting player checks for game to start
     */
    private void checkGameStarted() {
        //Create a thread to check if game is started
        new Thread(() -> {
            boolean started = false;
            while (!started) {
                started = databaseHandler.checkGameStarted(currentGame.getID());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Notify players that the game is started
            String[] playersInGame = databaseHandler.getPlayersInGame(currentGame.getID()).split(";");
            for (int i = 0; i < playersInGame.length; i = i + 2) {
                sendToInGamePlayers("update_player_names", playersInGame[i] + ";" + playersInGame[i + 1], true);
            }
            sendToInGamePlayers("game_started", "Game is started, Please wait for your turn;;", true);

            //change game state to "playing"
            databaseHandler.setGameState(currentGame.getID(), "playing");
            //databaseHandler.disconnectDatabase();
        }).start();
    }

    /**
     * Roll dices, generated 5 "dices" by generate 5 random numbers between 1 -6
     *
     * @return - return String with each dice separated with ;;
     */
    private String rollDices() {
        StringBuilder randomNumbers = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            if (i == 4) {
                randomNumbers.append(String.valueOf(rand.nextInt(6) + 1));
            } else {
                randomNumbers.append(rand.nextInt(6) + 1).append(";;");
            }
        }
        return randomNumbers.toString();
    }

    /**
     * Sends message to all players in the same game
     *
     * @param messageCode - messageCode to use for the message
     * @param message     - message to be sent, excluding the messagecode
     */
    private void sendToInGamePlayers(String messageCode, String message, boolean sendToSelf) {
        for (LinkedBlockingQueue<String> que : gameMessagesQueues) {
            if (!sendToSelf && que == playerMessageQueue) {

            } else {
                try {
                    String xmlString = xmlDocumentHandler.createXmlString(messageCode, player, message, currentGame);
                    synchronized (Lock) {
                        que.put(xmlString);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sends message to the communiting clients own queue
     *
     * @param messageCode - messageCode to use for the message
     * @param message     - message to be sent, excluding the messagecode
     */
    private void sendToMyMessageQueue(String messageCode, String message) {
        try {
            String xmmlString = xmlDocumentHandler.createXmlString(messageCode, player, message, currentGame);
            synchronized (Lock) {
                playerMessageQueue.put(xmmlString);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server started and accepting clients on port: " + DEFAULTPORT);
        /**
         * Creates Sockets for server and client connections
         */
        ServerSocket serverSocket = null;
        Socket clientSocket;

        /**
         * start the server
         */
        try {
            serverSocket = new ServerSocket(DEFAULTPORT);

            // wait for incoming connections from clients and spawn a new thread for each client
            // and waits for the next client to connect
            while (true) {
                clientSocket = serverSocket.accept();
                LinkedBlockingQueue<String> playerMessageQueue = new LinkedBlockingQueue<>();
                System.out.println("There is now a new player connected to the server.");
                YatzyServer yatzyServer = new YatzyServer(clientSocket, playerMessageQueue, args);
                Thread thread = new Thread(yatzyServer);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                //close connection if open
                if (serverSocket != null) {
                    serverSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void run() {
        boolean gameActive = true;

        //Creates reader and writer for communication to and from the client
        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;
        try {
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            PrintWriter finalSocketWriter = socketWriter;

            //Creates a thread for sending messages to the client
            new Thread(() -> {
                String mess;
                while (true) {
                    try {
                        //wait for a new message to arrive in the clients messagequeue and the send it to the client
                        mess = playerMessageQueue.take();
                        //Send the message to the client
                        finalSocketWriter.println(mess);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String incommingMessage = socketReader.readLine();
            //System.out.println(incommingMessage);

            //create a loop to check for incoming messages from player
            while (gameActive) {
                String messageCode = xmlDocumentHandler.parseXml(incommingMessage, "code");
                String message = xmlDocumentHandler.parseXml(incommingMessage, "body");
                switch (messageCode) {
                    case "chatt":
                        sendToInGamePlayers("chatt", message, true);
                        break;
                    case "invite_players":
                        invitePlayers(message);
                        break;
                    case "join_game":
                        joinPlayerToGame(message);
                        break;
                    case "login":
                        loginPlayer(message);
                        break;
                    case "new_user":
                        createNewUser(message);
                        break;
                    case "player_completed_game":
                        playerCompletedGame();
                        break;
                    case "roll_dices":
                        sendToMyMessageQueue("rolled_dices", rollDices());
                        break;
                    case "turn_completed":
                        turnCompleted(message);
                        break;
                }
                Thread.sleep(100);
                //Check for new messages
                try {
                    incommingMessage = socketReader.readLine();

                } catch (SocketException e) {
                    System.out.println("Player diconnected");
                    break;
                }
            }

        } catch (SocketException e) {
            System.out.println("Player diconnected");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

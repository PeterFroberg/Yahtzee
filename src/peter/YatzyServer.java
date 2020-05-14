package peter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class YatzyServer implements Runnable {

    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private Mailhandler mailhandler = new Mailhandler(System.getenv("mailServer"), System.getenv("mailUserName"), System.getenv("mailPassword"));

    private final static int DEFAULTPORT = 2000;
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<LinkedBlockingQueue<String>>> gamesMessagingQueues = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<LinkedBlockingQueue<String>> gameMessagesQueues; // = new CopyOnWriteArrayList<>();

    private final static Object Lock = new Object();
    private final Socket clientSocket;


    private LinkedBlockingQueue<String> playerMessageQueue;
    private Game currentGame = new Game();

    private YatzyServer(Socket clientSocket, LinkedBlockingQueue<String> playerMessageQueue) {
        this.clientSocket = clientSocket;
        this.playerMessageQueue = playerMessageQueue;
        //YahtzeeServer.gameMessagesQueues = gameMessageQueues;
    }

    private void createCommnunicationForGame() {
        //Create new gameMessagingArray
        //CopyOnWriteArrayList<LinkedBlockingQueue<String>> newGameMessagingQueuesArray = new CopyOnWriteArrayList<>();
        //Add clientsMessageArray to game
        gameMessagesQueues = new CopyOnWriteArrayList<>();
        gameMessagesQueues.add(playerMessageQueue);
        //gameMessagesQueues.add(playerMessageQueue);
        //Add gameMessagingArray to server MessagingArray
        //gamesMessagingQueues.put(gameID, gameMessagesQueues);
        gamesMessagingQueues.put(currentGame.getID(), gameMessagesQueues);
    }

    private void joinCommunicationForGame() {
        gameMessagesQueues = gamesMessagingQueues.get(currentGame.getID());
        //CopyOnWriteArrayList<LinkedBlockingQueue<String>> messagingQueueArrayToJoin = gamesMessagingQueues.get(gameID);
        gameMessagesQueues.add(playerMessageQueue);
    }

    private void checkGameStarted() {

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
            sendToMyMessageQueue("game_started::", "Game is started, Please wait for your turn");
            databaseHandler.setGameState(currentGame.getID(), "playing");
            //playNextTurn(gameID);
            //databaseHandler.disconnectDatabase();
        }).start();
    }

    private String rollDices(){
        StringBuilder randomNumbers = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < 5; i++){
            if(i == 4){
                randomNumbers.append(String.valueOf(rand.nextInt(6) + 1));
            }else{
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
                    synchronized (Lock) {
                        que.put(messageCode + message);
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
            synchronized (Lock) {
                playerMessageQueue.put(messageCode + message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server started");
        /**
         * Creates Sockets for server and client connections
         */
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        /**
         * start the server
         */
        try {
            serverSocket = new ServerSocket(DEFAULTPORT);
            //CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessageQueues = new CopyOnWriteArrayList<>();
            //LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

            /**
             * wait for incoming connections from clients and spawn a new thread for each client
             * and waits for the next client to connect
             */
            while (true) {
                clientSocket = serverSocket.accept();
                LinkedBlockingQueue<String> playerMessageQueue = new LinkedBlockingQueue<>();
                //clientMessageQueues.add(playerMessageQueue);
                //System.out.println("There is now: " + clientMessageQueues.size() + " players connected to the server.");
                System.out.println("There is now: a new player connected to the server.");
                YatzyServer yatzyServer = new YatzyServer(clientSocket, playerMessageQueue);
                Thread thread = new Thread(yatzyServer);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
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
        //int gameID = 0;

        /**
         * Creates reader and writer for communication to and from the client
         */
        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;
        try {
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            PrintWriter finalSocketWriter = socketWriter;

            /**
             * Creates a thread for sending messages to the client
             */
            new Thread(() -> {
                String mess;
                while (true) {
                    try {
                        /**
                         * wait for a new message to arrive in the clients messagequeue and the send it to the client
                         */
                        mess = playerMessageQueue.take();
                        //Send the message to the client
                        finalSocketWriter.println(mess);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /**
             * create a Thread to check for when to start game
             */

            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String incommingMessage = socketReader.readLine();
            System.out.println(incommingMessage);

            while (gameActive) {
                String[] parts = incommingMessage.split("::");
                String messageCode = parts[0];
                String message = parts[1];
                switch (messageCode) {
                    case "chatt":
                        sendToInGamePlayers("chatt::", message, true);
                        break;
                    case "login":
                        //DO LOGIN
                        databaseHandler.connectToDatabase();
                        String[] loginUserParts = message.split(";;");
                        Player player = databaseHandler.login(loginUserParts[0], loginUserParts[1]);
                        if (player != null) {
                            playerMessageQueue.put("login_user::" + player.getID() + ";;" + player.getName() + ";;" + player.getEmail());
                        } else {
                            playerMessageQueue.put("login_user::" + "-1");
                        }

                        //databaseHandler.disconnectDatabase();
                        break;
                    case "new_user":
                        //DO NEW USER
                        databaseHandler.connectToDatabase();
                        String[] newUserParts = message.split(";;");
                        int newDBID = databaseHandler.CreateNewPlayer(newUserParts[0], newUserParts[1], newUserParts[2]);
                        if (newDBID != 0) {
                            sendToMyMessageQueue("new_user::", String.valueOf(newDBID));
                        }
                        //databaseHandler.disconnectDatabase();
                        break;
                    case "invite_players":
                        databaseHandler.connectToDatabase();
                        String[] invitedPlayersParts = message.split(";");
                        String[] players = invitedPlayersParts[2].split(";");
                        currentGame.setNumberOfPlayers(players.length + 1);
                        currentGame.setID(databaseHandler.invitePlayers(invitedPlayersParts[0], players));
                        String body = "You are invited for a game of Yahtzee by " + invitedPlayersParts[0] + "\n Start the yahtzee program and use the \"join game\" option and input the game# " + String.valueOf(currentGame.getID()) + " in the play menu to join";
                        String result = "";
                        for (String playerToInvite : players) {
                            result = mailhandler.send(playerToInvite, "testarepostkurs@gmail.com", "Yahtzee invitation", body);
                        }
                        currentGame.setPositionInGame(1);
                        sendToMyMessageQueue("invitations::", currentGame.getPositionInGame() + ";;" + currentGame.getNumberOfPlayers() + ";;" + result);
                        databaseHandler.addPlayerToGame(currentGame.getID(), Integer.parseInt(invitedPlayersParts[1]));
                        createCommnunicationForGame();

                        checkGameStarted();

                        break;
                    case "join_game":
                        databaseHandler.connectToDatabase();
                        String[] joinGameParts = message.split(";");
                        int playerID = Integer.parseInt(joinGameParts[0]);
                        String playerEmail = joinGameParts[1];
                        currentGame.setID(Integer.parseInt(joinGameParts[2]));
                        String playerAdded = databaseHandler.joinGame(playerID, playerEmail, currentGame.getID());
                        String[] playerAddedToGameParts = playerAdded.split(";;");
                        currentGame.setPositionInGame(Integer.parseInt(playerAddedToGameParts[1]));
                        currentGame.setNumberOfPlayers(databaseHandler.getNumberOfPlayersInGame(currentGame.getID()));
                        sendToMyMessageQueue("player_added_to_game::", playerAddedToGameParts[1] + ";;" + currentGame.getNumberOfPlayers() + ";;" + playerAddedToGameParts[2]);
                        joinCommunicationForGame();
                        checkGameStarted();
                        //databaseHandler.disconnectDatabase();
                        break;
                    case "turn_completed":
                        currentGame.setCurrentTurn(currentGame.getCurrentTurn() + 1);
                        String[] turnCompetedParts = message.split(";;");
                        int nextPlayer = currentGame.getPositionInGame() + 1;
                        if(nextPlayer > currentGame.getNumberOfPlayers()){
                            nextPlayer = 1;
                        }
                        String newMessage = nextPlayer + ";;" + currentGame.getPositionInGame() + ";;" + turnCompetedParts[0] + ";;" + turnCompetedParts[1];
                        sendToInGamePlayers("players_turn::",newMessage, false);
                        break;
                    case "roll_dices":
                        sendToMyMessageQueue("rolled_dices::", rollDices());
                        //sendToMyMessageQueue("rolled_dices::", "3;;3;;6;;6;;6");
                        break;
                    case "player_completed_game":
                        currentGame.increasePlayersCompletedGame();
                        if(currentGame.getNumberOfPlayers() - currentGame.getPlayersCompletedGame() == 0){
                            sendToInGamePlayers("game_completed::", "na", true);
                        }else{
                            sendToInGamePlayers("player_completed_game::", "na", false);
                        }
                        break;
                }
                Thread.sleep(100);
                //Check for new messages
                try {
                    incommingMessage = socketReader.readLine();
                } catch (SocketException e) {
                    e.printStackTrace();
                    break;
                }
                System.out.println("Received:" + incommingMessage);
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            gameMessagesQueues.remove(playerMessageQueue);
        }

    }

}

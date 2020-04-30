package peter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class YahtzeeServer implements Runnable {

    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private Mailhandler mailhandler = new Mailhandler(System.getenv("mailServer"), System.getenv("mailUserName"), System.getenv("mailPassword"));

    private final static int DEFAULTPORT = 2000;
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<LinkedBlockingQueue<String>>> gamesMessagingQueues = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessagesQueues;

    private final static Object Lock = new Object();
    private final Socket clientSocket;

    private LinkedBlockingQueue<String> clientMessageQueue;

    private YahtzeeServer(Socket clientSocket, LinkedBlockingQueue<String> clientMessageQueue, CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessageQueues) {
        this.clientSocket = clientSocket;
        this.clientMessageQueue = clientMessageQueue;
        YahtzeeServer.clientMessagesQueues = clientMessageQueues;
    }

    @Override
    public void run() {

        boolean gameActive = true;

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
                        mess = (String) clientMessageQueue.take();
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
            new Thread(() ->{
                while(!databaseHandler.startGame){

                }

            });


            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String incommingMessage = socketReader.readLine();
            System.out.println(incommingMessage);

            //while (incommingMessage != null) {
            while (gameActive) {
                //incommingMessage = socketReader.readLine();
                String[] parts = incommingMessage.split("::");
                String messageCode = parts[0];
                String message = parts[1];
                switch (messageCode) {
                    case "chatt":
                        sendToInGamePlayers("chatt::", message);
                        break;
                    case "login":
                        //DO LOGIN
                        databaseHandler.connectToDatabase();
                        String[] loginUserParts = message.split(";;");
                        Player player = databaseHandler.login(loginUserParts[0], loginUserParts[1]);
                        //if(player != null){
                        for (LinkedBlockingQueue<String> que : clientMessagesQueues) {
                            if (que == clientMessageQueue) {
                                if (player != null) {
                                    que.put("login_user::" + player.getID() + ";;" + player.getName() + ";;" + player.getEmail());
                                } else {
                                    que.put("login_user::" + "-1");
                                }
                            }
                        }
                        databaseHandler.disconnectDatabase();
                        break;
                    case "new_user":
                        //DO NEW USER
                        databaseHandler.connectToDatabase();
                        String[] newUserParts = message.split(";;");
                        int newDBID = databaseHandler.CreateNewPlayer(newUserParts[0], newUserParts[1], newUserParts[2]);
                        if (newDBID != 0) {
                            sendToMyMessageQueue("new_user::", String.valueOf(newDBID));
                        }
                        databaseHandler.disconnectDatabase();
                        break;
                    case "invite_players":
                        databaseHandler.connectToDatabase();
                        String[] invitedPlayersParts = message.split(";");
                        String[] players = invitedPlayersParts[2].split(";");
                        int newGameID = databaseHandler.invitePlayers(invitedPlayersParts[0], players);
                        String body = "You are invited for a game of Yahtzee by " + invitedPlayersParts[0] + "\n Start the yahtzee program and use the \"join game\" option and input the game# " + String.valueOf(newGameID) +" in the play menu to join";
                        String result = "";
                        for (String playerToInvite : players) {
                            result = mailhandler.send(playerToInvite, "testarepostkurs@gmail.com", "Yahtzee invitation", body);
                        }
                        sendToMyMessageQueue("invitations::", result);
                        databaseHandler.addPlayerToGame(newGameID, Integer.parseInt(invitedPlayersParts[1]));
                        createCommnunicationForGame(newGameID, clientMessageQueue);
                        databaseHandler.disconnectDatabase();
                        break;
                    case "join_game":
                        databaseHandler.connectToDatabase();
                        String[] joinGameParts = message.split(";");
                        int playerID = Integer.parseInt(joinGameParts[0]);
                        String playerEmail = joinGameParts[1];
                        int gameID = Integer.parseInt(joinGameParts[2]);

                        String playerAdded = databaseHandler.joinGame(playerID,playerEmail,gameID);
                        sendToMyMessageQueue("player_added_to_game::", playerAdded);
                        joinCommunicationForGame(Integer.parseInt(joinGameParts[2]), clientMessageQueue);
                        databaseHandler.disconnectDatabase();
                        break;
                }
                Thread.sleep(100);
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
        } finally {
            clientMessagesQueues.remove(clientMessageQueue);
        }

    }

    private void createCommnunicationForGame(int gameID, LinkedBlockingQueue<String> clientMessageQueue){
        //Create new gameMessagingArray
        CopyOnWriteArrayList<LinkedBlockingQueue<String>> newGameMessagingQueuesArray = new CopyOnWriteArrayList<>();
        //Add clientsMessageArray to game
        newGameMessagingQueuesArray.add(clientMessageQueue);
        //Add gameMessagingArray to server MessagingArray
        gamesMessagingQueues.put(gameID, newGameMessagingQueuesArray);
    }

    private void joinCommunicationForGame(int gameID, LinkedBlockingQueue<String> clientMessageQueue){
        CopyOnWriteArrayList<LinkedBlockingQueue<String>> messagingQueueArrayToJoin = gamesMessagingQueues.get(gameID);
        messagingQueueArrayToJoin.add(clientMessageQueue);
    }

    private boolean startGame(){
        new Thread(() ->{
            startGame()

        });
    }
    /**
     * Sends message to all players in the same game
     *
     * @param messageCode - messageCode to use for the message
     * @param message     - message to be sent, excluding the messagecode
     */
    private void sendToInGamePlayers(String messageCode, String message) {
        for (LinkedBlockingQueue<String> que : clientMessagesQueues) {
            try {
                synchronized (Lock) {
                    que.put(messageCode + message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        for (LinkedBlockingQueue<String> que : clientMessagesQueues) {
            if (que == clientMessageQueue) {
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
            CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessageQueues = new CopyOnWriteArrayList<>();
            LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();

            /**
             * wait for incoming connections from clients and spawn a new thread for each client
             * and waits for the next client to connect
             */
            while (true) {
                clientSocket = serverSocket.accept();
                LinkedBlockingQueue<String> clientMessageQueue = new LinkedBlockingQueue<>();
                clientMessageQueues.add(clientMessageQueue);
                System.out.println("There is now: " + clientMessageQueues.size() + " players connected to the server.");
                YahtzeeServer yahtzeeServer = new YahtzeeServer(clientSocket, clientMessageQueue, clientMessageQueues);
                Thread thread = new Thread(yahtzeeServer);
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
}

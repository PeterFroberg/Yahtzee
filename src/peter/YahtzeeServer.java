package peter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class YahtzeeServer implements Runnable {

    private final static int DEFAULTPORT = 2000;
    private static CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessagesQueues;

    private final static Object Lock = new Object();

    private final Socket clientSocket;


    private LinkedBlockingQueue<String> clientMessageQueue;

    private YahtzeeServer(Socket clientSocket, LinkedBlockingQueue<String> clientMessageQueue, CopyOnWriteArrayList<LinkedBlockingQueue<String>> clientMessageQueues){
        this.clientSocket = clientSocket;
        this.clientMessageQueue = clientMessageQueue;
        YahtzeeServer.clientMessagesQueues = clientMessageQueues;
    }

    @Override
    public void run() {
        /**
         * Creates reader and writer for communication to and from the client
         */
        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;


    }
    public static void main(String[] args){
        int port;
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
        while (true){
            clientSocket = serverSocket.accept();
            LinkedBlockingQueue<String> clientMessageQueue = new LinkedBlockingQueue<>();
            clientMessageQueues.add(clientMessageQueue);

            YahtzeeServer yahtzeeServer = new YahtzeeServer(clientSocket,clientMessageQueue,clientMessageQueues);
            Thread thread = new Thread(yahtzeeServer);
            thread.start();
        }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

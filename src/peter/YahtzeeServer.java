package peter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try{
            socketWriter = new PrintWriter(clientSocket.getOutputStream(),true);
            PrintWriter finalSocketWriter =socketWriter;

            new Thread(() -> {
                String mess;
                while (true){
                    try{
                        /**
                         * wait for a new message to arrive in the clients messagequeue and the send it to the client
                         */
                        mess = (String)clientMessageQueue.take();
                        //Send the message to the client
                        finalSocketWriter.println(mess);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine = socketReader.readLine();

            ////Kontrolera vilket typ av meddelande och hantera det

            //Om det är chatt

            while (inputLine != null){
                synchronized (Lock){
                    for(LinkedBlockingQueue que : clientMessagesQueues){
                        if(que != clientMessageQueue){
                            que.put("PlayerName: " + inputLine);
                        }
                    }
                }
                Thread.sleep(100);
                try{
                    inputLine = socketReader.readLine();
                }catch (SocketException e){
                    e.printStackTrace();
                    break;
                }
            }


        }catch (SocketException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            clientMessagesQueues.remove(clientMessageQueue);
        }


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
        }finally{

            try{
                if(serverSocket!=null){
                    serverSocket.close();
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }
}

package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Petar Kresoja
 */
public class ChatServer {

    public static Vector<ClientHandler> clients = new Vector<>();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {
        infoOut("Server started...");
        try {
            ServerSocket ss = new ServerSocket(765);
            Socket s;
            while (true) {
                infoOut("Waiting for client requests...");
                s = ss.accept();
                infoOut("New client request: " + s);
                ClientHandler ch = new ClientHandler(s);
                Thread t = new Thread(ch);
                clients.add(ch);
                t.start();
                infoOut("Client " + ch.getName() + " connected successfuly");
            }
        } catch (IOException ex) {
            errorOut(ex);
        }
    }

    public static void infoOut(String output) {
        Date date = new Date();
        System.out.println("[INFO: " + DATE_FORMAT.format(date) + "] >> " + output);
    }

    public static void errorOut(Exception ex) {
        Date date = new Date();
        System.err.println("[ERROR: " + DATE_FORMAT.format(date) + "] >> Internal exception occurred, " + ex.getMessage());
    }
}

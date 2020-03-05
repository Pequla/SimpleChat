package chatserver;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

/**
 *
 * @author Petar Kresoja
 */
public class ChatServer {

    // CUVA KLIJENTE U VEKTORU
    public static Vector<ClientHandler> ar = new Vector<>();

    public static void main(String[] args) throws IOException {

        // OTVARA TCP PORT 765
        ServerSocket ss = new ServerSocket(765);
        Socket s;

        // MSG
        consoleOut("ChatServer started");

        // SERVER LOOP
        while (true) {

            // PRIHVATA KLIJENTA
            s = ss.accept();
            consoleOut("New client request received: " + s);

            // NOVI HANDELER OBJEKAT
            consoleOut("Creating a new handler...");
            ClientHandler mtch = new ClientHandler(s);

            // PRAVI NOVI THREAD ZA HANDELER 
            Thread t = new Thread(mtch);
            consoleOut("Added to active client list");

            // DODAJE KLIJENTA U AKTIVNU LISTU
            ar.add(mtch);

            // ZAPOCNI THREAD 
            t.start();

        }
    }

    // DRY SISTEMSKI IZLAZ
    public static void consoleOut(String consoleOut) {

        // ISPIS DATUMA I VREMENA
        // 2019/12/01 12:15:48
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        // FORMIRA IZLAZ
        System.out.println(">> [" + dateFormat.format(date) + "] " + consoleOut);

    }
}

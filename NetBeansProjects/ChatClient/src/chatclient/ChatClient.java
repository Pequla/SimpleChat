package chatclient;

/**
 *
 * @author Petar Kresoja
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {

    // TCP PORT
    final static int SERVER_PORT = 765;

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        Scanner scn = new Scanner(System.in);
        System.out.println("ChatClient starting...");

        // UCITAVA IP ADRESU SERVERA
        System.out.print("Type in server IP adress: ");
        String ipAdress = scn.nextLine();

        // TRAZI SERVER NA TOJ ADRESI
        InetAddress ip = InetAddress.getByName(ipAdress);

        // POVEZUJE SE 
        Socket s = new Socket(ip, SERVER_PORT);

        // PRIMA I/O STREAM 
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // PRIHVATA IME 
        while (true) {
            System.out.print("Input your name: ");
            String clientName = scn.nextLine();
            dos.writeUTF(clientName);
            clientName = dis.readUTF();
            if (clientName.equals("usernameError")) {
                System.out.println("Invalid or already taken username, try again");
            } else {
                System.out.println("Client name set to: " + clientName);
                break;
            }
        }

        // POVEZAO SE
        System.out.println("Connected to server " + ipAdress + " !");
        System.out.println("Send messages in format <message>/<recipient> !");
        System.out.println("Type in logout to EXIT");

        // SLANJE PORUKA 
        Thread sendMessage = new Thread(() -> {
            while (true) {

                // PROVERAVA PORUKU
                String msg = scn.nextLine();
                System.out.println(">> You: " + msg);

                try {
                    // ISPIS U OUTPUT STREAM
                    dos.writeUTF(msg);

                    // LOGOUT/EXIT
                    if (msg.equals("logout")) {
                        System.exit(0);
                    }

                } catch (IOException ex) {
                    // STOP
                    System.err.println("Server dropped connection");
                    System.exit(0);
                }
            }
        });

        // CITANJE PORUKA
        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    // CITA PORUKU POSLATU OVOM KLIJETU
                    String msg = dis.readUTF();
                    System.out.println(msg);
                } catch (IOException ex) {
                    // STOP
                    System.err.println("Server dropped connection");
                    System.exit(0);
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}

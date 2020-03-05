package chatserver;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Petar Kresoja
 */
class ClientHandler implements Runnable {

    private List<String> usernames = new ArrayList<>();
    private final String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    private Socket s;
    boolean isloggedin;

    // KONSTRUKTOR
    public ClientHandler(Socket s) throws IOException {
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
        this.s = s;
        this.isloggedin = true;

        // UCITAVA IME KLIJENTA
        String clientName;
        while (true) {

            String clientNameInput = dis.readUTF();
            clientName = clientNameInput.toLowerCase();
            if (usernames.contains(clientName) || clientName.equals("")) {
                dos.writeUTF("usernameError");
            } else {
                dos.writeUTF(clientName);
                ChatServer.consoleOut("Client name: " + clientName);
                break;
            }
        }

        this.name = clientName;
        usernames.add(clientName);

    }

    @Override
    public void run() {
        String received;
        OUTER:
        while (true) {
            try {
                received = dis.readUTF();
                switch (received) {
                    case "logout":
                        this.isloggedin = false;
                        this.s.close();
                        while (usernames.contains(name)) {
                            usernames.remove(name);
                        }
                        ChatServer.consoleOut("Client " + name + " logged out");
                        break OUTER;
                    case "":
                        break;
                    default:
                        ChatServer.consoleOut("Client " + name + " input: " + received);
                        break;
                }
                StringTokenizer st = new StringTokenizer(received, "/");
                int stCounter = st.countTokens();
                if (stCounter == 2) {
                    String MsgToSend = st.nextToken();
                    String recipient = st.nextToken();

                    // TRAZI PRIMAOCA U AKTIVNOJ LISTI 
                    for (ClientHandler ch : ChatServer.ar) {

                        // AKO POSTOJI KLIJENT POSALJI MU: 
                        if (ch.name.equals(recipient) && ch.isloggedin == true) {
                            ch.dos.writeUTF(this.name + " : " + MsgToSend);
                            break;
                        }
                    }
                }
            } catch (java.net.SocketException e) {
                try {
                    System.err.println("Client " + this.name + " has dropped connection");
                    this.isloggedin = false;
                    this.s.close();
                    while (usernames.contains(this.name)) {
                        usernames.remove(this.name);
                    }
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }

            } catch (IOException e) {
                // DEBUG
                e.printStackTrace(System.out);
            }
        }

        try {
            // ZATVARA RESURSE 
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            // DEBUG
            e.printStackTrace(System.out);
        }
    }
}

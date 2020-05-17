package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Petar Kresoja
 */
public class ClientHandler implements Runnable {

    private String name;
    private boolean online;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientHandler(Socket s) {
        try {
            this.online = true;
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
            ChatServer.infoOut("Retrieving client name...");
            String ci = dis.readUTF().trim().toUpperCase();
            if (isAvailable(ci)) {
                dos.writeUTF("NAME OK");
                this.name = ci;
            } else {
                dos.writeUTF("ERROR");
            }
        } catch (IOException ex) {
            ChatServer.errorOut(ex);
        }
    }

    @Override
    public void run() {
        String received;
        while (online) {
            try {
                received = dis.readUTF();
                if (received.startsWith("/")) {
                    if (received.equals("/logout")) {
                        disconnect();
                        break;
                    } else if (received.equals("/username")) {
                        ChatServer.infoOut("Client " + this.getName() + " issued the username command");
                        serverOut("Your username is: " + this.name);
                    } else if (received.equals("/list")) {
                        ChatServer.infoOut("Client " + this.getName() + " issued the list command");
                        ArrayList list = new ArrayList();
                        ChatServer.clients.forEach((ch) -> {
                            list.add(ch.getName());
                        });
                        serverOut("Active user list: " + list.toString());
                    } else if (received.startsWith("/tell")) {
                        String[] split = received.trim().split("\\s+", 3);
                        if (split.length == 3) {
                            if (!isRecipient(split)) {
                                serverOut("The user you specified doesnt exist !");
                            }
                        } else {
                            serverOut("Invalid or not complete tell command !");
                        }
                    } else {
                        serverOut("The command doesnt exist !");
                    }
                } else {
                    send(this.getName() + ": " + received);
                }
            } catch (SocketException se) {
                ChatServer.errorOut(se);
                disconnect();
                break;
            } catch (IOException ex) {
                ChatServer.errorOut(ex);
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    private static boolean isAvailable(String name) {
        boolean available = true;
        if (name.contains(" ")) {
            available = false;
        } else {
            for (ClientHandler ch : ChatServer.clients) {
                if (ch.getName().equals(name)) {
                    available = false;
                }
            }
        }
        return available;
    }

    private boolean isRecipient(String[] split) throws IOException {
        boolean value = false;
        String recipient = split[1].toUpperCase();
        String message = split[2];
        for (ClientHandler ch : ChatServer.clients) {
            if (ch.getName().equals(recipient)) {
                ChatServer.infoOut("Client " + this.getName() + " whispered to " + recipient + ": " + message);
                dos.writeUTF("You whispered to " + recipient + ": " + message);
                ch.dos.writeUTF(this.getName() + ": " + message);
                value = true;
                break;
            }
        }
        return value;
    }

    private void disconnect() {
        send("Client " + this.getName() + " disconnected");
        ChatServer.clients.remove(this);
        try {
            dos.close();
            dis.close();
        } catch (IOException ex) {
            ChatServer.errorOut(ex);
        }
        online = false;
    }

    private void send(String message) {
        ChatServer.infoOut(message);
        ChatServer.clients.forEach((ch) -> {
            try {
                ch.dos.writeUTF(message);
            } catch (IOException ex) {
                ChatServer.errorOut(ex);
            }
        });
    }

    private void serverOut(String output) {
        try {
            this.dos.writeUTF("SERVER: " + output);
        } catch (IOException ex) {
            ChatServer.errorOut(ex);
        }
    }
}

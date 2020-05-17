package chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author Petar Kresoja
 */
public class ChatClient {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("[HH:mm] ");

    public static void main(String[] args) throws UnknownHostException, IOException {

        String address = null;
        String username = null;
        int port = 0;

        try {
            File config = new File("config.properties");
            Properties prop = new Properties();
            if (!config.exists()) {
                try (OutputStream output = new FileOutputStream(config)) {
                    prop.setProperty("host.adress", "localhost");
                    prop.setProperty("host.port", "765");
                    prop.setProperty("username", "username");
                    prop.store(output, "SimpleChat configuration file" + System.lineSeparator() + "Created by: Pequla ( https://pequla.github.io/ )");
                }
            }
            try (InputStream input = new FileInputStream(config)) {
                prop.load(input);
                address = prop.getProperty("host.adress");
                port = Integer.valueOf(prop.getProperty("host.port"));
                username = prop.getProperty("username");
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        consoleOut("ChatClient starting...");
        Socket s = new Socket(InetAddress.getByName(address), port);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        dos.writeUTF(username);
        if (dis.readUTF().equals("NAME OK")) {

            Scanner scn = new Scanner(System.in);

            consoleOut("Connected to the server...");
            consoleOut("Type in /logout to EXIT");

            Thread sendMessage = new Thread(() -> {
                while (true) {
                    try {
                        String msg = scn.nextLine().trim();
                        if (msg.length() != 0) {
                            dos.writeUTF(msg);
                            if (msg.equals("/logout")) {
                                System.exit(0);
                            }
                        }
                    } catch (IOException ex) {
                        errMessage(ex);
                        break;
                    }
                }
            });

            Thread readMessage = new Thread(() -> {
                while (true) {
                    try {
                        consoleOut(dis.readUTF());
                    } catch (IOException ex) {
                        errMessage(ex);
                        break;
                    }
                }
            });

            sendMessage.start();
            readMessage.start();
        } else {
            consoleOut("Invalid or already taken username !");
            consoleOut("Please select another one in config.properties !");
            System.exit(0);
        }
    }

    public static void consoleOut(String text) {
        Date date = new Date();
        System.out.println(DATE_FORMAT.format(date) + text);
    }

    private static void errMessage(Exception ex) {
        System.out.println("Unexpected error occured !");
        System.out.println("More info: " + ex.getMessage());
        System.exit(0);
    }
}

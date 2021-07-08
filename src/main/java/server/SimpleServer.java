package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SimpleServer extends WebSocketServer {

    HashMap<String, ArrayList<WebSocket>> connexions = new HashMap<>();
    ServerSocket loggerServer = null;

    public SimpleServer(InetSocketAddress address) {
        super(address);
        System.out.println("Simple server started");

        this.connexions.put("logger", new ArrayList<>());
        this.connexions.put("loggerListener", new ArrayList<>());
        this.connexions.put("robots", new ArrayList<>());

        try {
            this.loggerServer = new ServerSocket(1664);
            System.out.println("Log server started");
            this.runLoggerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runLoggerServer()
    {
        (new Thread(() -> {
            while (true) {
                try {
                    Socket socket = this.loggerServer.accept();
                    System.out.println("Connexion au logs");
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line = reader.readLine();
                    System.out.println("COUCOU : " + line);

                    if (line == null) {
                        continue;
                    }

                    if (line.contains("INFO : init logger")) {
                        LoggerListenerThread logger = new LoggerListenerThread(socket, this);
                        logger.start();
                    }

                    Thread.sleep(5);
                } catch (IOException | InterruptedException e) {
                    System.out.println("I/O error: " + e);
                }
            }
        })).start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        System.out.println("new connection: " + handshake.getResourceDescriptor());
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
        if (this.connexions.get("loggerListener").contains(conn)) {
            this.connexions.get("loggerListener").remove(conn);
        }
        if (this.connexions.get("robot").contains(conn)) {
            this.connexions.get("robot").remove(conn);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        if (this.connexions.get("loggerListener").contains(conn)) {
            // On veut écouter des logs
        } else if (this.connexions.get("robots").contains(conn)) {
            // Un robot annonce un truc
            ArrayList<WebSocket> robotSockets = new ArrayList<>();
            for (WebSocket socket: this.connexions.get("robots")) {
                if (!conn.equals(socket)) {
                    robotSockets.add(socket);
                }
            }
            broadcast(message, robotSockets);
        } else {
            String channel = null;
            switch (message) {
                case "loggerListener":
                    channel = "loggerListener";
                    break;
                case "robot":
                    channel = "robots";
                    break;
            }
            if (channel != null) {
                this.connexions.get(channel).add(conn);
            } else {
                System.out.println("No channel for " + message);
            }
        }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        System.out.println("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        System.out.println("server started successfully");
    }

    public void broadcastLogMessage(String message) {
        broadcast(message, this.connexions.get("loggerListener"));
    }

    public static void main(String[] args) {
        String host = "192.168.0.104";
        int port = 4269;

        WebSocketServer server = new SimpleServer(new InetSocketAddress(host, port));
        server.run();
    }
}

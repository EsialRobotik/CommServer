package server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class SimpleServer extends WebSocketServer {

    HashMap<String, ArrayList<WebSocket>> connexions = new HashMap<>();

    public SimpleServer(InetSocketAddress address) {
        super(address);

        this.connexions.put("logger", new ArrayList<>());
        this.connexions.put("loggerListener", new ArrayList<>());
        this.connexions.put("robots", new ArrayList<>());
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
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        if (this.connexions.get("logger").contains(conn)) {
            // On reçoit des logs
            broadcast(message, this.connexions.get("loggerListener"));
        } else if (this.connexions.get("loggerListener").contains(conn)) {
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
            if (message.contains("INFO : init logger")) {
                channel = "logger";
            } else {
                switch (message) {
                    case "loggerListener":
                        channel = "loggerListener";
                        break;
                    case "robot":
                        channel = "robots";
                        break;
                }
            }
            if (channel != null) {
                this.connexions.get(channel).add(conn);
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


    public static void main(String[] args) {
        String host = "localhost";
        int port = 4269;

        WebSocketServer server = new SimpleServer(new InetSocketAddress(host, port));
        server.run();
    }
}

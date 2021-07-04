package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        Socket socket = null;

        HashMap<String, ArrayList<SocketThread>> threads = new HashMap<>();
        threads.put("logger", new ArrayList<>());
        threads.put("loggerListener", new ArrayList<>());
        threads.put("robots", new ArrayList<>());

        System.out.println("Server start");

        try {
            serverSocket = new ServerSocket(4269);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Connexion");
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                System.out.println("COUCOU : " + line);

                if (line == null) {
                    continue;
                }

                if (line.contains("INFO : init logger")) {
                    SocketThread logger = new LoggerThread(socket, threads);
                    threads.get("logger").add(logger);
                    logger.start();
                } else {
                    switch (line) {
                        case "loggerListener":
                        case "GET / HTTP/1.1":
                            SocketThread loggerListener = new LoggerListenerThread(socket, threads);
                            threads.get("loggerListener").add(loggerListener);
                            loggerListener.start();
                            break;
                        case "robot":
                            SocketThread robotThread = new RobotThread(socket, threads);
                            threads.get("robots").add(robotThread);
                            robotThread.start();
                            break;
                        case "echo":
                            new EchoThread(socket).start();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
        }
    }

}

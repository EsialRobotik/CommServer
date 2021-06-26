package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LoggerThread extends SocketThread {

    public LoggerThread(Socket clientSocket, HashMap<String, ArrayList<SocketThread>> threads) throws IOException {
        this.socket = clientSocket;
        this.threads = threads;
        this.createReaderAndWriter();
    }

    public void run() {
        try {
            System.out.println("server.LoggerThread start");
            String line;
            while (true) {
                line = reader.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    System.out.println("server.LoggerThread - " + line);
                    for (SocketThread loggerListener : threads.get("loggerListener")) {
                        loggerListener.write(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

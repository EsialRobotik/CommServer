package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LoggerListenerThread extends Thread {

    protected Socket socket;
    protected SimpleServer simpleServer;

    protected BufferedReader reader;
    protected PrintWriter writer;

    public LoggerListenerThread(Socket clientSocket, SimpleServer simpleServer) throws IOException {
        this.socket = clientSocket;
        this.simpleServer = simpleServer;
        this.createReaderAndWriter();
    }

    protected void createReaderAndWriter() throws IOException {
        InputStream input = this.socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = this.socket.getOutputStream();
        this.writer = new PrintWriter(output, true);
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
                } else if (line.length() == 0) {
                    continue;
                } else {
                    System.out.println("server.LoggerThread - " + line);
                    this.simpleServer.broadcastLogMessage(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

public class LoggerListenerThread extends Thread {

    protected Socket socket;
    protected SimpleServer simpleServer;

    protected BufferedReader reader;
    protected PrintWriter writer;

    private PrintWriter fileWriter;

    public LoggerListenerThread(Socket clientSocket, SimpleServer simpleServer) throws IOException {
        this.socket = clientSocket;
        this.simpleServer = simpleServer;
        this.createReaderAndWriter();
        this.createFileWriter();
    }

    protected void createReaderAndWriter() throws IOException {
        InputStream input = this.socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = this.socket.getOutputStream();
        this.writer = new PrintWriter(output, true);
    }

    private void createFileWriter() throws IOException {
        File logDir = new File("./logs");
        if (!logDir.exists()) logDir.mkdir();
        // Create the file named `<date>-<client_ip>-log.txt`
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = simpleDateFormat.format(new Date());
        String clientIp = this.socket.getInetAddress().getHostAddress();
        String fileName = "logs/" + date + "_" + clientIp + "_log.txt";
        this.fileWriter = new PrintWriter(new FileWriter(fileName, false));
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
                    if (fileWriter != null) {
                        fileWriter.println(line);
                        fileWriter.flush();
                    }
                    this.simpleServer.broadcastLogMessage(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

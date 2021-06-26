package server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RobotThread extends SocketThread {

    public RobotThread(Socket clientSocket, HashMap<String, ArrayList<SocketThread>> threads) throws IOException {
        this.socket = clientSocket;
        this.threads = threads;
        this.createReaderAndWriter();
    }

    public void run() {
        try {
            System.out.println("server.RobotThread start");
            String line;
            while (true) {
                line = reader.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    System.out.println("server.RobotThread - " + line);
                    for (SocketThread loggerListener : threads.get("robots")) {
                        if (!loggerListener.equals(this)) {
                            loggerListener.write(line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

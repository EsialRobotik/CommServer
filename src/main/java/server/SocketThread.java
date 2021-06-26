package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

abstract public class SocketThread extends Thread {
    protected Socket socket;
    protected HashMap<String, ArrayList<SocketThread>> threads;

    protected BufferedReader reader;
    protected PrintWriter writer;

    protected void createReaderAndWriter() throws IOException {
        InputStream input = this.socket.getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(input));

        OutputStream output = this.socket.getOutputStream();
        this.writer = new PrintWriter(output, true);
    }

    public void write(String message) {
        this.writer.println(message);
    }
}

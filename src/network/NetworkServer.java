package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal single-client socket server. Accept one client and relay messages if needed.
 * Start with: new NetworkServer(55555).start();
 */
public class NetworkServer {
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private Socket clientSocket;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    public NetworkServer(int port) {
        this.port = port;
    }

    public void start() {
        exec.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                running = true;
                System.out.println("Server listening on port " + port);
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                // This server only accepts connection; any message relay can be implemented here
                // For example: read lines and print or forward to host game logic.
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                while (running && (line = in.readLine()) != null) {
                    System.out.println("From client: " + line);
                    // If you want, process or forward messages here
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            } finally {
                stop();
            }
        });
    }

    public void stop() {
        running = false;
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
        exec.shutdownNow();
        System.out.println("Server stopped.");
    }
}

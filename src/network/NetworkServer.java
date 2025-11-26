package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A very simple single-client TCP server designed for multiplayer testing
 * in Bomberman-style games.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *     <li>Open a listening port</li>
 *     <li>Accept one incoming client</li>
 *     <li>Print or relay any received messages</li>
 * </ul>
 *
 * This implementation is intentionally minimal. It can be expanded later
 * to support multiple clients or synchronized game state updates.
 *
 * Usage:
 * <pre>
 * NetworkServer server = new NetworkServer(55555);
 * server.start();
 * </pre>
 */
public class NetworkServer {

    /** Port number this server listens on. */
    private final int port;

    /** Underlying server socket. */
    private ServerSocket serverSocket;

    /** Whether the server should continue accepting/reading messages. */
    private volatile boolean running = false;

    /** Connected client socket (single connection). */
    private Socket clientSocket;

    /** Thread for accepting the client and listening to messages. */
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    /**
     * Creates a new socket server listening on the given port.
     *
     * @param port TCP port to bind to
     */
    public NetworkServer(int port) {
        this.port = port;
    }

    /**
     * Starts the server asynchronously.
     * <p>
     * The server:
     * <ul>
     *     <li>Binds the port</li>
     *     <li>Waits for one client to connect</li>
     *     <li>Prints all incoming messages to console</li>
     * </ul>
     */
    public void start() {
        exec.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                running = true;

                System.out.println("Server listening on port " + port);

                // Accept exactly one client
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while (running && (line = in.readLine()) != null) {
                    System.out.println("From client: " + line);
                    // TODO: Game logic could be added here.
                }

            } catch (IOException e) {
                if (running) e.printStackTrace();
            } finally {
                stop();
            }
        });
    }

    /**
     * Stops the server and releases all network resources.
     * Also terminates the background executor.
     */
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

package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A minimal TCP client used for sending and receiving text-based
 * multiplayer messages.
 *
 * <p>This class handles:</p>
 * <ul>
 *     <li>Connecting to a remote server</li>
 *     <li>Sending formatted messages (movement, bomb placement, pixel coords)</li>
 *     <li>Listening for incoming messages on a background thread</li>
 *     <li>Forwarding received messages to {@link MultiplayerListener}</li>
 * </ul>
 *
 * Typical usage:
 * <pre>
 * MultiplayerListener listener = msg -> System.out.println(msg);
 * NetworkClient client = new NetworkClient("192.168.1.10", 55555, listener);
 * client.connect();
 * client.send("POS,5,3,left");
 * client.close();
 * </pre>
 */
public class NetworkClient {

    /** Host address of the game server. */
    private final String host;

    /** Port number to connect to. */
    private final int port;

    /** Listener that receives incoming messages from server. */
    private final MultiplayerListener listener;

    /** Underlying TCP socket. */
    private Socket socket;

    /** Output stream (UTF-8, auto-flush). Sends messages to server. */
    private PrintWriter out;

    /** Input stream used to read messages line-by-line. */
    private BufferedReader in;

    /** Dedicated background thread to listen for incoming messages. */
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    /** Flag indicating if the listening loop is active. */
    private volatile boolean running = false;

    /**
     * Creates a new client instance.
     *
     * @param host     server IP address (e.g., "127.0.0.1")
     * @param port     server port
     * @param listener callback triggered when server sends messages
     */
    public NetworkClient(String host, int port, MultiplayerListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    /**
     * Attempts to connect to the server. Initializes socket, streams,
     * and starts the asynchronous listener loop.
     *
     * @return true if successful, false otherwise
     */
    public boolean connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 3000); // 3s timeout

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            running = true;
            startListenLoop();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Starts an asynchronous loop that continuously reads messages from the server.
     * Whenever a line is received, {@link MultiplayerListener#onNetworkMessage(String)}
     * is invoked.
     */
    private void startListenLoop() {
        exec.submit(() -> {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    if (listener != null) {
                        listener.onNetworkMessage(line);
                    }
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            } finally {
                close();
            }
        });
    }

    /**
     * Sends a raw string message to the server.
     *
     * @param msg a line of text (newline appended automatically)
     */
    public void send(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    /**
     * Sends a tile-based position packet.
     *
     * Format: {@code POS,row,col,direction}
     *
     * @param row hero/drone row index
     * @param col hero/drone col index
     * @param dir movement direction string
     */
    public void sendPosition(int row, int col, String dir) {
        send(String.format("POS,%d,%d,%s", row, col, dir));
    }

    /**
     * Sends pixel-based position (higher resolution).
     *
     * Format: {@code PIX,x,y,direction}
     *
     * @param x   pixel X
     * @param y   pixel Y
     * @param dir direction string
     */
    public void sendPixelPosition(int x, int y, String dir) {
        send(String.format("PIX,%d,%d,%s", x, y, dir));
    }

    /**
     * Sends a bomb placement event.
     *
     * Format: {@code BOMB,row,col}
     *
     * @param row tile row where the bomb is placed
     * @param col tile column where the bomb is placed
     */
    public void sendBomb(int row, int col) {
        send(String.format("BOMB,%d,%d", row, col));
    }

    /**
     * Closes the client connection, stops the listener, and frees all resources.
     */
    public void close() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}

        exec.shutdownNow();
    }
}

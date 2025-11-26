package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal client to connect to the server, send messages, and receive them via callback.
 *
 * Usage:
 *   NetworkClient client = new NetworkClient("127.0.0.1", 55555, listener);
 *   client.connect();
 *   client.send("POS,5,3,left");
 *   client.close();
 */
public class NetworkClient {
    private final String host;
    private final int port;
    private final MultiplayerListener listener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    public NetworkClient(String host, int port, MultiplayerListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

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

    public void send(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    public void sendPosition(int row, int col, String dir) {
        send(String.format("POS,%d,%d,%s", row, col, dir));
    }

    public void sendPixelPosition(int x, int y, String dir) {
        send(String.format("PIX,%d,%d,%s", x, y, dir));
    }

    public void sendBomb(int row, int col) {
        send(String.format("BOMB,%d,%d", row, col));
    }

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

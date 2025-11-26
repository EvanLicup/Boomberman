package model;

import network.*;
import model.Boomberman;

import javax.swing.*;
import java.awt.*;

/**
 * Simple main menu UI for launching the Boomberman game.
 * <p>
 * Provides options for:
 * <ul>
 *   <li>Multiplayer (host or join)</li>
 *   <li>Single-player (start Player vs Bot)</li>
 *   <li>Quit</li>
 * </ul>
 * <p>
 * Networking is supported via {@link NetworkServer} and {@link NetworkClient}, and
 * incoming messages are delivered to {@link #onNetworkMessage(String)}.
 */
public class MainMenu {

    private JFrame frame;
    private NetworkServer server;
    private NetworkClient client;

    // Default port
    private final int PORT = 55555;

    /**
     * Creates and shows the main menu on the Swing event thread.
     */
    public MainMenu() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    /**
     * Builds the Swing UI and wires button actions.
     * <p>
     * This method constructs the JFrame, title label, and three buttons:
     * Multiplayer, Single Player, and Quit. Button actions launch dialogs,
     * start the game, or exit the application.
     */
    private void createAndShow() {
        frame = new JFrame("Boomberman - Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 240);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Boomberman", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        root.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton pvp = new JButton("Multiplayer");
        JButton pvb = new JButton("Single Player");
        JButton quit = new JButton("Quit");

        pvp.setAlignmentX(Component.CENTER_ALIGNMENT);
        pvb.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttons.add(pvp);
        buttons.add(Box.createRigidArea(new Dimension(0, 12)));
        buttons.add(pvb);
        buttons.add(Box.createRigidArea(new Dimension(0, 12)));
        buttons.add(quit);

        root.add(buttons, BorderLayout.CENTER);
        frame.setContentPane(root);

        // Button actions
        pvp.addActionListener(ev -> showPvPDialog());
        pvb.addActionListener(ev -> startPlayerVsBot());
        quit.addActionListener(ev -> {
            if (client != null) client.close();
            if (server != null) server.stop();
            System.exit(0);
        });

        frame.setVisible(true);
    }

    /**
     * Shows a dialog asking whether to Host or Join a multiplayer game.
     * <p>
     * Selecting Host will start a local server and connect a local client.
     * Selecting Join prompts for a host IP and tries to connect.
     */
    private void showPvPDialog() {
        String[] options = {"Host", "Join", "Cancel"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Host or Join a multiplayer game?",
                "Player vs Player",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options, options[0]);

        if (choice == 0) hostGame();
        else if (choice == 1) joinGame();
    }

    /**
     * Starts a local server and connects a local client to it (localhost).
     * <p>
     * On failure the server is stopped and an error dialog is shown.
     */
    private void hostGame() {
        // Start server
        server = new NetworkServer(PORT);
        server.start();

        // Local client connects to host
        String host = "127.0.0.1";
        MultiplayerListener listener = this::onNetworkMessage;

        client = new NetworkClient(host, PORT, listener);
        boolean ok = client.connect();

        if (!ok) {
            JOptionPane.showMessageDialog(frame, "Failed to connect local client to host.", "Error", JOptionPane.ERROR_MESSAGE);
            server.stop();
            server = null;
            return;
        }

        JOptionPane.showMessageDialog(frame, "Hosting server started.\nMultiplayer gameplay setup pending.", "Hosting", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prompts the user for a host IP and attempts to connect as a client.
     * On connection failure, notifies the user with an error dialog.
     */
    private void joinGame() {
        String host = JOptionPane.showInputDialog(frame, "Enter host IP:", "127.0.0.1");

        if (host == null || host.trim().isEmpty()) return;

        MultiplayerListener listener = this::onNetworkMessage;
        client = new NetworkClient(host.trim(), PORT, listener);

        boolean ok = client.connect();
        if (!ok) {
            JOptionPane.showMessageDialog(frame, "Could not connect to host.", "Error", JOptionPane.ERROR_MESSAGE);
            client = null;
            return;
        }

        JOptionPane.showMessageDialog(frame, "Connected to host.\nMultiplayer gameplay setup pending.", "Connected", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Launches the single-player Boomberman game (Player vs Bot).
     * <p>
     * This method disposes the menu frame and calls {@link Boomberman#main(String[])}.
     */
    private void startPlayerVsBot() {

        // Close main menu
        frame.dispose();

        try {
            // Start your real game
            Boomberman.main(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to start Boomberman.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Callback invoked when a network message is received from a remote player.
     * <p>
     * TODO: integrate network messages into the game model / remote player handling.
     *
     * @param msg received textual message from network
     */
    private void onNetworkMessage(String msg) {
        System.out.println("NetMsg: " + msg);
        // TODO: integrate with GameModel + remoteHero
    }

    /**
     * Launches the MainMenu application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new MainMenu();
    }
}

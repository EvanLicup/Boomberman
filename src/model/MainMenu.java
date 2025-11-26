package model;

import network.*;
import model.Boomberman;

import javax.swing.*;
import java.awt.*;

/**
 * Simple homepage with: Player vs Player | Player vs Bot | Quit
 */
public class MainMenu {

    private JFrame frame;
    private NetworkServer server;
    private NetworkClient client;

    // Default port
    private final int PORT = 55555;

    public MainMenu() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

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
     * Launch Boomberman game normally (Player vs Bot)
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
     * This receives network messages from remote players.
     */
    private void onNetworkMessage(String msg) {
        System.out.println("NetMsg: " + msg);
        // TODO: integrate with GameModel + remoteHero
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}

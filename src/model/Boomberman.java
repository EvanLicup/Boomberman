package model;

import controller.KeyHandler;
import gamethread.Gamethread;

import javax.swing.*;

/**
 * The Boomberman class contains the main method that runs the Bomberman-inspired game.
 * <p>
 * It initializes the game window, game model, key inputs, and game panel,
 * then starts the main game thread which handles updating and rendering.
 */
public class Boomberman {

    /**
     * Main entry point of the Boomberman game.
     * <p>
     * The method performs the following:
     * <ul>
     *   <li>Creates the main application window</li>
     *   <li>Initializes the {@link GameModel}, {@link GamePanel}, and {@link KeyHandler}</li>
     *   <li>Attaches the panel to the window and makes it visible</li>
     *   <li>Starts the {@link Gamethread} which updates and renders the game</li>
     * </ul>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("Boomberman");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);

        KeyHandler keyH = new KeyHandler();
        GameModel gameModel = new GameModel(keyH);
        GamePanel gamePanel = new GamePanel(gameModel);

        mainWindow.add(gamePanel);
        mainWindow.pack();

        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);

        Gamethread gamethread = new Gamethread(gameModel, gamePanel);
        gamethread.startGameThread();
    }

    /**
     * Displays UI information for the hero, such as controls or heart count.
     * <p>
     * NOTE: This method is currently unused and does not contain an implementation.
     *
     * @param bomberman the hero whose UI information should be displayed
     */
    public void displayHeroUI(Hero bomberman) {
        // This method has no implementation (placeholder for future UI logic)
    }
}

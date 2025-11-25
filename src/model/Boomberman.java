package model;

import javax.swing.*;

/**
 * The main.Boomberman class contains the main method that runs the Bomberman-Inspired game.
 * It initializes the game board, hero, and bomb, then loops to process
 * player input, update the game state, and display the board until the
 * game ends.
 */
public class Boomberman {

    /**
     * Main method: entry point of the main.Boomberman game.
     * Initializes the board, hero, bomb, and input scanner.
     * Runs the main game loop that does the ff:
     * - UI/Board Display
     * - Player Input
     * - Delegates movement or bomb placement to the main.Hero object
     * - Updates bomb countdown and explosion via object.Bomb methods
     * - Checks Win/Loss condiitons
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("main.Boomberman");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);
        GamePanel gamePanel = new GamePanel();
        mainWindow.add(gamePanel);
        mainWindow.pack();

        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);

        gamePanel.startGameThread();


    }

    /**
     * Displays the user interface for the hero, including controls
     * and current number of hearts.
     *
     * @param bomberman the main.Hero object whose hearts are displayed
     */

}

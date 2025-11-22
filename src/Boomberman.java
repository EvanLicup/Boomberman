import javax.swing.*;
import java.util.Scanner;

/**
 * The Boomberman class contains the main method that runs the Bomberman-Inspired game.
 * It initializes the game board, hero, and bomb, then loops to process
 * player input, update the game state, and display the board until the
 * game ends.
 */
public class Boomberman {

    /**
     * Main method: entry point of the Boomberman game.
     * Initializes the board, hero, bomb, and input scanner.
     * Runs the main game loop that does the ff:
     * - UI/Board Display
     * - Player Input
     * - Delegates movement or bomb placement to the Hero object
     * - Updates bomb countdown and explosion via Bomb methods
     * - Checks Win/Loss condiitons
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        int gameOver = 0;      // flag to indicate when the game ends
        int endProgram = 0;    // flag to indicate when to exit the program
        int rows = 7;
        int cols = 11;
        int winCoordX = 0;     // X-coordinate of the win tile
        int winCoordY = 8;     // Y-coordinate of the win tile

        // Character-based sample map made by the developers
        char[][] inputMap = {
                {'D',' ','D',' ',' ','D',' ',' ','D','D',' '},
                {'D','I',' ','I','D','I','D','I',' ','I',' '},
                {' ',' ','D',' ',' ','D',' ','D',' ','D','D'},
                {' ','I',' ','I','D','I',' ','I',' ','I','D'},
                {'D',' ','D','D',' ','D','D',' ','D',' ',' '},
                {' ','I','D','I',' ','I',' ','I','D','I','D'},
                {'D',' ',' ','D',' ',' ',' ','D','D','D',' '}
        };



        // Initialize the tile board and game objects
        Tile[][] TileMap = new Tile[rows][cols];
        GameBoard mainBoard = new GameBoard(inputMap, inputMap.length, inputMap[0].length, TileMap);
        // Initialize bomb at (0,0) with countdown = 4.
        // Note: isActive is false by default, so it won't affect the board
        // until the hero places it with the 'H' command.
        NormalBomb bomb = new NormalBomb(0, 0, 4);
        Hero bomberman = new Hero(6, 5, 3);          // starting position and hearts
        Scanner sc = new Scanner(System.in);
        char e; // input character

        while (endProgram == 0) {
            while (gameOver == 0)
            {
                System.out.println("BOOMBERMAN");         // display game title
                mainBoard.displayBoard(bomberman, bomb);  // display board
                displayUI(bomberman);                     // display player UI
                e = sc.next().charAt(0);                  // get user input

                bomberman.moveHero(e, mainBoard, bomb);  // move hero or place bomb

                // Check win condition
                if (bomberman.getX() == mainBoard.getWinCoordinateX() && bomberman.getY() == mainBoard.getWinCoordinateY()) {
                    System.out.println("Congratulations! You have entered the Exit Tile");
                    gameOver = 1;

                }

                // Bomb countdown and explosion
                if (bomberman.getActiveBombStatus()) {
                    bomb.decrementCountdown();
                }
                if (bomb.getTurnsTillExplosion() == 0 && bomberman.getActiveBombStatus()) {

                    int heartsBeforeDetonate = bomberman.getHearts();
                    bomb.explode(mainBoard, bomberman);
                    System.out.println("The bomb has exploded!");
                if (bomberman.getHearts() < heartsBeforeDetonate) {
                    if (bomberman.getHearts() <= 0) {
                        System.out.println("Game Over!");
                        gameOver = 1;
                    } else {
                    System.out.println("The bomb exploded on yo face!");
                        }
                    }
                }
            }
            // Final board display after game ends
            mainBoard.displayBoard(bomberman, bomb);
            System.out.println("Enter Any Key to Exit Program");
            e = sc.next().charAt(0);
            endProgram = 1;

        
        }


        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("Boomberman");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setResizable(false);

        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);


    }

    /**
     * Displays the user interface for the hero, including controls
     * and current number of hearts.
     *
     * @param bomberman the Hero object whose hearts are displayed
     */
    public static void displayUI(Hero bomberman) {
        System.out.println("Hearts Remaining: " + bomberman.getHearts());
        System.out.println("W - Up");
        System.out.println("S - Down");
        System.out.println("A - Left");
        System.out.println("D - Right");
        System.out.println("H - Place Bomb");
        System.out.print("Enter move: ");
    }

}

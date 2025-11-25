package main;

import object.Bomba;

import java.awt.*;

/**
 * Represents the player-controlled hero in the game board.
 * The hero has a position, a limited number of hearts (lives),
 * and can place one active bomb at a time.
 */
public class Hero {
    /** Current row position of the hero on the board. */
    private int x;

    /** Current column position of the hero on the board. */
    private int y;

    /** Character used to visually represent the hero on the board. */
    private char symbol = '@';

    /** Number of hearts (lives) remaining for the hero. */
    private int hearts;

    /** Indicates whether the hero currently has an active bomb placed. */
    private boolean hasActiveBomb = false;



    // ADDED
    private int heroSpeed = 5;
    public String direction = "left";

    GamePanel gamePanel;
    KeyHandler keyH;

    public Rectangle hitBox = new Rectangle(32, 48, 32, 42);
    boolean collision = false;



    /**
     * Constructs a main.Hero object and initializes its starting position and hearts.
     *
     * @param x      the initial row position of the hero
     * @param y      the initial column position of the hero
     * @param hearts the initial number of hearts (lives) the hero starts with
     */
    public Hero(int x, int y, int hearts, GamePanel gamePanel, KeyHandler keyH) {
        this.x = 4 * gamePanel.tileSize;
        this.y = 3 * gamePanel.tileSize;
        this.hearts = hearts;
        this.gamePanel = gamePanel;
        this.keyH = keyH;
    }


    /**
     * Decrements the hero's heart count by one, typically when hit by an explosion or even an enemy for future development.
     */
    public void loseHeart() {
        this.hearts--;
    }


    /**
     * Returns the current row position of the hero.
     *
     * @return the hero's x-coordinate (row index)
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current column position of the hero.
     *
     * @return the hero's y-coordinate (column index)
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the symbol used to visually represent the hero.
     *
     * @return the character symbol representing the hero
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Validates whether a movement to a given tile is within the bounds
     * of the board and whether that tile is walkable.
     *
     * @param moveX the target row to move to
     * @param moveY the target column to move to
     * @param board the game board used to check boundaries and walkability
     * @return {@code true} if the target tile is inside bounds and walkable, {@code false} otherwise
     */
    /*
    public boolean isValidated(int moveX, int moveY, GameBoard board) {
        if (moveX < 0 || moveX >= board.getRows() || moveY < 0 || moveY >= board.getCols()) {
            return false;
        }
        boolean nextMove = board.tileBoard[moveX][moveY].isWalkable();

        if (nextMove == false) {
            return false;
        }

        return true;
    }

     */


    /** Updates the hero's active bomb status.
     * @param choice {@code true} if a bomb is now active, {@code false} once the bomb has exploded
     */
    public void setHasActiveBomb(boolean choice) {
        hasActiveBomb = choice;
    }

    /** @return the remaining hearts of the hero */
    public int getHearts() {
        return hearts;
    }

    /** @return {@code true} if a bomb is currently active, {@code false} otherwise */
    public boolean getActiveBombStatus() {
        return hasActiveBomb;
    }

    // ADDED


    public void update() {

        if (keyH.upPressed  == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
            if (keyH.upPressed) {
                direction = "up";
            }
            else if (keyH.downPressed) {
                direction = "down";
            }
            else if (keyH.leftPressed) {
                direction = "left";
            }
            else if (keyH.rightPressed) {
                direction = "right";
            }

            collision = false;
            gamePanel.cChecker.checkTile(this);


            if (collision == false) {
                switch (direction) {
                    case "up":
                        y -= heroSpeed;
                        break;
                    case "down":
                        y += heroSpeed;
                        break;
                    case "left":
                        x -= heroSpeed;
                        break;
                    case "right":
                        x += heroSpeed;
                        break;
                }
            }
        }

        if (keyH.placePressed == true) {
            int centerX = getX() + gamePanel.tileSize/ 2;
            int centerY = getY() + gamePanel.tileSize/ 2;
            Bomba b = new Bomba(centerX/ gamePanel.tileSize,centerY / gamePanel.tileSize, 3.0);
            gamePanel.bombs.add(b);
            keyH.placePressed = false;
        }

    }




    public int getHeroSpeed() {
        return heroSpeed;
    }
}
package model;

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

    /** Number of hearts (lives) remaining for the hero. */
    private int hearts;

    // ADDED
    private int heroSpeed = 5;
    public String direction = "left";

    GameModel gm;
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
    public Hero(int x, int y, int hearts, GameModel gm, KeyHandler keyH) {
        this.x = 4 * gm.tileSize;
        this.y = 3 * gm.tileSize;
        this.hearts = hearts;
        this.gm = gm;
        this.keyH = keyH;
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

    /** @return the remaining hearts of the hero */
    public int getHearts() {
        return hearts;
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
            gm.cChecker.checkTile(this);


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
            boolean hasActiveBomb = false;
            for (Bomba b : gm.bombs) {
                if (!b.exploded) {
                    hasActiveBomb = true;
                    break;
                }
            }
            if (hasActiveBomb == false) {
                int centerX = getX() + gm.tileSize/ 2;
                int centerY = getY() + gm.tileSize/ 2;
                Bomba b = new Bomba(centerX/ gm.tileSize,centerY / gm.tileSize, 3.0);
                gm.bombs.add(b);

            }
            keyH.placePressed = false;

        }

    }




    public int getHeroSpeed() {
        return heroSpeed;
    }
}
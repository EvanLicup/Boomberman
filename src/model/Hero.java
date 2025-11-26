package model;

import controller.KeyHandler;

import java.awt.*;

/**
 * Represents the player-controlled hero in the game board.
 * The hero has a position, a limited number of hearts (lives),
 * and can place one active bomb at a time.
 */
public class Hero {
    /** Horizontal pixel position (X). Column = x / tileSize */
    private int x;

    /** Vertical pixel position (Y). Row = y / tileSize */
    private int y;

    /** Number of hearts (lives) remaining for the hero. */
    private int hearts = 3;

    // ADDED
    private int heroSpeed = 5;
    public String direction = "down";

    GameModel gm;
    KeyHandler keyH;

    public Rectangle hitBox = new Rectangle(32, 48, 32, 42);
    boolean collision = false;


    /**
     * Constructs a main.Hero object and initializes its starting position and hearts.
     *
     * @param x      the initial horizontal pixel position (X). To convert tile column -> multiply by tileSize before passing
     * @param y      the initial vertical pixel position (Y). To convert tile row -> multiply by tileSize before passing
     * @param hearts the initial number of hearts (lives) the hero starts with
     */
    public Hero(int x, int y, int hearts, GameModel gm, KeyHandler keyH) {
        this.x = x * gm.tileSize;
        this.y = y * gm.tileSize;
        this.gm = gm;
        this.keyH = keyH;
    }

    /**
     * Returns the current horizontal pixel position of the hero.
     *
     * @return the hero's x-coordinate (pixel X)
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current vertical pixel position of the hero.
     *
     * @return the hero's y-coordinate (pixel Y)
     */
    public int getY() {
        return y;
    }

    /** @return the remaining hearts of the hero */
    public int getHearts() {
        return hearts;
    }

    public void loseHeart() {
        hearts--;
    }

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
                // pass (row, col) -> (centerY/tileSize, centerX/tileSize)
                Bomba b = new Bomba(centerY / gm.tileSize, centerX / gm.tileSize, 3.0, gm);
                gm.bombs.add(b);

            }
            keyH.placePressed = false;

        }

    }




    // Helper getters for clarity (pixel vs tile coords)

    // Return raw pixel coords (sprite top-left)
    public int getPixelX() { return x; }
    public int getPixelY() { return y; }

    // Return hero center pixel (uses hitBox) - robust reference for tile occupancy
    public int getCenterPixelX() {
        return this.getX() + this.hitBox.x + (this.hitBox.width / 2);
    }

    public int getCenterPixelY() {
        return this.getY() + this.hitBox.y + (this.hitBox.height / 2);
    }

    // Convert the center pixel to tile column/row using integer division
    public int getTileCol() {
        // column = center pixel X / tileSize
        return getCenterPixelX() / gm.tileSize;
    }
    public int getTileRow() {
        // row = center pixel Y / tileSize
        return getCenterPixelY() / gm.tileSize;
    }

    public int getHeroSpeed() {
        return heroSpeed;
    }
}

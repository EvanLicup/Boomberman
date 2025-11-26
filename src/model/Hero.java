package model;

import controller.KeyHandler;

import java.awt.*;

/**
 * Represents the player-controlled hero in the game board.
 * The hero has a position, a limited number of hearts (lives),
 * and can place one active bomb at a time.
 */
public class Hero extends Entity {
    /** Number of hearts (lives) remaining for the hero. */
    private int hearts = 3;

    // ADDED
    private int heroSpeed = 5;
    public String direction = "down";

    GameModel gm;
    KeyHandler keyH;

    public Rectangle hitBox = new Rectangle(32, 48, 32, 42);
    boolean collision = false;




    public Hero(GameModel gm, KeyHandler keyH) {
        this.gm = gm;
        this.keyH = keyH;
    }

    public void setDefaultValues() {
        x = 100;
        y = 400;
        speed = heroSpeed;
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
                Bomba b = new Bomba(centerX/ gm.tileSize,centerY / gm.tileSize, 3.0, gm);
                gm.bombs.add(b);

            }
            keyH.placePressed = false;

        }

    }




    public int getHeroSpeed() {
        return heroSpeed;
    }
}
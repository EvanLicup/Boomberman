package model;

import java.awt.*;

public class drone {
    private int x;
    private int y;
    private int speed;
    public String direction;

    GameModel gm;
    public Rectangle hitBox = new Rectangle(32, 48, 32, 42);

    boolean collision = false;

    public drone(int x, int y, GameModel gm, String direction) {
        this.gm = gm;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    /**
     * Returns the current row position of the hero.
     *
     * @return the drone's x-coordinate (row index)
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current column position of the hero.
     *
     * @return the drone's y-coordinate (column index)
     */
    public int getY() {
        return y;
    }

    public void update() {

        // Check collision with tiles
        boolean collision = false;

        // Move
        if (!collision) {
            if (direction.equals("left")) {
                x -= speed;
            } else if (direction.equals("right")) {
                x += speed;
            }
        } else {
            // Reverse direction on collision
            direction = direction.equals("left") ? "right" : "left";
        }


    }
}

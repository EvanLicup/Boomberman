package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class drone {

    // pixel position
    private int x;
    private int y;

    // movement speed (slow)
    private int speed = 2;

    // "up", "down", "left", or "right"
    public String direction;

    // axis mode: true = horizontal, false = vertical
    private boolean horizontalMode;

    private boolean collision = false;

    private GameModel gm;

    public Rectangle hitBox = new Rectangle(8, 8, 32, 32);

    public BufferedImage spriteUp, spriteDown, spriteLeft, spriteRight;

    private final Random rand = new Random();

    public drone(int tileCol, int tileRow, GameModel gm) {
        this.gm = gm;

        // convert tile → pixel
        this.x = tileCol * gm.tileSize;
        this.y = tileRow * gm.tileSize;

        // determine if this drone moves horizontally or vertically
        horizontalMode = rand.nextBoolean(); // 50% horizontal, 50% vertical

        if (horizontalMode) {
            // horizontal movement only
            direction = rand.nextBoolean() ? "left" : "right";
        } else {
            // vertical movement only
            direction = rand.nextBoolean() ? "up" : "down";
        }
    }

    // getters
    public int getX() { return x; }
    public int getY() { return y; }

    public int getCenterPixelX() {
        return this.x + hitBox.x + (hitBox.width / 2);
    }
    public int getCenterPixelY() {
        return this.y + hitBox.y + (hitBox.height / 2);
    }

    public int getTileCol() { return getCenterPixelX() / gm.tileSize; }
    public int getTileRow() { return getCenterPixelY() / gm.tileSize; }

    public void update() {

        int nextX = x;
        int nextY = y;

        // move only along chosen axis
        switch (direction) {
            case "left":
                nextX -= speed;
                break;
            case "right":
                nextX += speed;
                break;
            case "up":
                nextY -= speed;
                break;
            case "down":
                nextY += speed;
                break;
        }

        // check collision
        if (!canMoveTo(nextX, nextY)) {
            reverseDirection();  // bounce back
        } else {
            x = nextX;
            y = nextY;
        }
    }

    private void reverseDirection() {
        switch (direction) {
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
        }
    }

    private boolean canMoveTo(int nextX, int nextY) {

        int tileSize = gm.tileSize;

        // hitbox corners
        int left = nextX + hitBox.x;
        int right = nextX + hitBox.x + hitBox.width - 1;
        int top = nextY + hitBox.y;
        int bottom = nextY + hitBox.y + hitBox.height - 1;

        int topRow = top / tileSize;
        int bottomRow = bottom / tileSize;
        int leftCol = left / tileSize;
        int rightCol = right / tileSize;

        // out of bounds
        if (topRow < 0 || leftCol < 0 || bottomRow >= gm.tiles.length || rightCol >= gm.tiles[0].length) {
            return false;
        }

        // tile collision
        Tile t1 = gm.tiles[topRow][leftCol];
        Tile t2 = gm.tiles[topRow][rightCol];
        Tile t3 = gm.tiles[bottomRow][leftCol];
        Tile t4 = gm.tiles[bottomRow][rightCol];

        return t1.isWalkable() && t2.isWalkable() && t3.isWalkable() && t4.isWalkable();
    }

    // sprites injected by GamePanel
    public void setSprites(BufferedImage up, BufferedImage down, BufferedImage left, BufferedImage right) {
        spriteUp = up;
        spriteDown = down;
        spriteLeft = left;
        spriteRight = right;
    }

    public BufferedImage getCurrentSprite() {
        return switch (direction) {
            case "up" -> spriteUp;
            case "down" -> spriteDown;
            case "left" -> spriteLeft;
            default -> spriteRight;
        };
    }
}

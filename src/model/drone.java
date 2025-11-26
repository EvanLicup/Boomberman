package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Represents an enemy drone that moves either horizontally or vertically across the board.
 * <p>
 * A drone:
 * <ul>
 *   <li>moves in one axis-only direction (chosen randomly at spawn),</li>
 *   <li>bounces when hitting non-walkable tiles,</li>
 *   <li>has a hitbox for collision checks,</li>
 *   <li>can be killed by bomb explosions, showing a death sprite for a duration.</li>
 * </ul>
 */
public class drone {

    /** Drone's pixel X position. */
    private int x;

    /** Drone's pixel Y position. */
    private int y;

    /** Movement speed in pixels per update. */
    protected int speed = 2;

    /** Current movement direction: "up", "down", "left", or "right". */
    public String direction;

    /** Determines if this drone moves horizontally (true) or vertically (false). */
    private boolean horizontalMode;

    /** Keeps track of collision detection results. */
    private boolean collision = false;

    /** Reference to the game model containing tiles and settings. */
    private GameModel gm;

    /** Hitbox for collision checking (relative to sprite). */
    public Rectangle hitBox = new Rectangle(8, 8, 32, 32);

    /** Sprites for directional animation. */
    public BufferedImage spriteUp, spriteDown, spriteLeft, spriteRight;

    /** Sprite used when the drone is killed. */
    public BufferedImage spriteDeath;

    /** Random generator for movement initialization. */
    private final Random rand = new Random();

    /** Whether the drone is dead. */
    private boolean dead = false;

    /** Timestamp when death was triggered. */
    private long deathAt = 0L;

    /** Time in milliseconds to keep showing the death sprite. */
    private static final long DEATH_MS = 600L;

    /**
     * Constructs a new drone at the given tile position.
     *
     * @param tileCol the column position in tile units
     * @param tileRow the row position in tile units
     * @param gm the game model used for map access and tile size
     */
    public drone(int tileCol, int tileRow, GameModel gm) {
        this.gm = gm;

        // convert tile → pixel
        this.x = tileCol * gm.tileSize;
        this.y = tileRow * gm.tileSize;

        // determine if this drone moves horizontally or vertically
        horizontalMode = rand.nextBoolean(); // 50% horizontal, 50% vertical

        if (horizontalMode) {
            direction = rand.nextBoolean() ? "left" : "right";
        } else {
            direction = rand.nextBoolean() ? "up" : "down";
        }
    }

    // getters

    /**
     * Sets the drone's movement speed.
     *
     * @param s speed value (must be > 0)
     */
    public void setSpeed(int s) {
        if (s > 0) speed = s;
    }

    /**
     * Returns the movement speed.
     *
     * @return current speed value
     */
    public int getSpeed() {
        return speed;
    }

    /** @return drone's pixel X coordinate */
    public int getX() { return x; }

    /** @return drone's pixel Y coordinate */
    public int getY() { return y; }

    /** @return center X pixel coordinate based on hitbox */
    public int getCenterPixelX() {
        return this.x + hitBox.x + (hitBox.width / 2);
    }

    /** @return center Y pixel coordinate based on hitbox */
    public int getCenterPixelY() {
        return this.y + hitBox.y + (hitBox.height / 2);
    }

    /** @return tile column of the drone's center */
    public int getTileCol() { return getCenterPixelX() / gm.tileSize; }

    /** @return tile row of the drone's center */
    public int getTileRow() { return getCenterPixelY() / gm.tileSize; }

    /**
     * Updates drone movement, bouncing off walls and stopping if dead.
     */
    public void update() {

        // if dead, don't move (we keep sprite displayed until GameModel removes it)
        if (dead) {
            return;
        }

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

    /**
     * Reverses the current direction (called when blocked).
     */
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

    /**
     * Determines whether the drone can move to the given pixel coordinates.
     *
     * @param nextX next X position in pixels
     * @param nextY next Y position in pixels
     * @return true if tile collision does not occur, false otherwise
     */
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

    /**
     * Injects directional sprites rendered by {@link GamePanel}.
     *
     * @param up sprite used when moving up
     * @param down sprite used when moving down
     * @param left sprite used when moving left
     * @param right sprite used when moving right
     */
    public void setSprites(BufferedImage up, BufferedImage down, BufferedImage left, BufferedImage right) {
        spriteUp = up;
        spriteDown = down;
        spriteLeft = left;
        spriteRight = right;
    }

    /**
     * Sets the sprite used when the drone dies.
     *
     * @param death the death sprite image
     */
    public void setDeathSprite(BufferedImage death) {
        this.spriteDeath = death;
    }

    /**
     * Marks the drone as dead, starts death timer, and stops movement.
     */
    public void markAsDead() {
        if (dead) return;
        dead = true;
        deathAt = System.currentTimeMillis();
        direction = "dead";
    }

    /**
     * Returns whether the drone is currently dead.
     *
     * @return {@code true} if dead, otherwise {@code false}
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Determines whether the death animation (death sprite) has finished showing.
     *
     * @return true if the death sprite duration has expired
     */
    public boolean isDeathExpired() {
        if (!dead) return false;
        return (System.currentTimeMillis() - deathAt) >= DEATH_MS;
    }

    /**
     * Returns the correct sprite for drawing based on direction or death state.
     *
     * @return the sprite image to render
     */
    public BufferedImage getCurrentSprite() {
        if (dead) {
            return spriteDeath;
        }
        return switch (direction) {
            case "up" -> spriteUp;
            case "down" -> spriteDown;
            case "left" -> spriteLeft;
            default -> spriteRight;
        };
    }
}

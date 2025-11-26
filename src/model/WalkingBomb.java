package model;

import controller.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a remote-controlled bomb that the player can move independently
 * from the {@link Hero}.
 * <p>
 * A {@code WalkingBomb} behaves similarly to the hero in terms of collision:
 * it has a hitbox, respects map boundaries, and only moves over walkable tiles.
 * Movement is controlled through dedicated bomb-movement keys handled by
 * {@link KeyHandler} (bombUpPressed, bombDownPressed, bombLeftPressed, bombRightPressed).
 * <p>
 * The bomb does NOT explode automatically. Detonation is triggered externally
 * by {@code GameModel} when {@code keyH.detonatePressed} becomes true.
 * The explosion has an extended radius (cross shape, distance 2).
 */
public class WalkingBomb {

    /** Horizontal pixel position (top–left of the sprite). */
    private int x;

    /** Vertical pixel position (top–left of the sprite). */
    private int y;

    /** Column index of the bomb on the tile grid, based on its center position. */
    private int tileCol;

    /** Row index of the bomb on the tile grid, based on its center position. */
    private int tileRow;

    /**
     * Collision hitbox relative to (x,y).
     * Used to check tile collisions in the same way drones/Hero are handled.
     */
    public Rectangle hitBox = new Rectangle(8, 8, 32, 32);

    /** Reference to the game model for tile access, destruction, and hero/drones interactions. */
    private final GameModel gm;

    /** Key handler used to move the walking bomb independently of the hero. */
    private final KeyHandler keyH;

    /** Movement speed in pixels per update. */
    private final int speed = 4;

    /** True once the bomb has exploded (prevents further movement or updates). */
    private boolean exploded = false;

    /** Upwards-facing sprite. */
    public BufferedImage spriteUp;
    /** Downwards-facing sprite. */
    public BufferedImage spriteDown;
    /** Left-facing sprite. */
    public BufferedImage spriteLeft;
    /** Right-facing sprite. */
    public BufferedImage spriteRight;

    /** The sprite currently shown depending on last movement direction. */
    private BufferedImage spriteCurrent;

    /**
     * Creates a {@code WalkingBomb} at a specified tile position.
     *
     * @param startCol the initial tile column
     * @param startRow the initial tile row
     * @param gm       reference to the {@link GameModel}
     * @param keyH     controller handling movement input
     */
    public WalkingBomb(int startCol, int startRow, GameModel gm, KeyHandler keyH) {
        this.gm = gm;
        this.keyH = keyH;
        this.tileCol = startCol;
        this.tileRow = startRow;
        this.x = startCol * gm.tileSize;
        this.y = startRow * gm.tileSize;
        this.spriteCurrent = null; // default until movement occurs
    }

    /**
     * Assigns directional movement sprites to this walking bomb.
     *
     * @param up    sprite when moving up
     * @param down  sprite when moving down
     * @param left  sprite when moving left
     * @param right sprite when moving right
     */
    public void setSprites(BufferedImage up, BufferedImage down, BufferedImage left, BufferedImage right) {
        this.spriteUp = up;
        this.spriteDown = down;
        this.spriteLeft = left;
        this.spriteRight = right;
    }

    /**
     * @return the sprite representing the bomb's current facing direction
     */
    public BufferedImage getCurrentSprite() {
        return spriteCurrent;
    }

    /** @return the bomb's current tile row */
    public int getTileRow() { return tileRow; }

    /** @return the bomb's current tile column */
    public int getTileCol() { return tileCol; }

    /** @return the bomb's pixel X position */
    public int getX() { return x; }

    /** @return the bomb's pixel Y position */
    public int getY() { return y; }

    /** @return true if the bomb has already exploded */
    public boolean isExploded() { return exploded; }

    /**
     * Updates the walking bomb's movement and position, based on input.
     * <p>
     * - Reads bomb-movement keys (not hero keys).  
     * - Performs tile-based collision checks.  
     * - Updates tileCol/tileRow using the bomb's center pixel.  
     * - Clamps position within map bounds.  
     */
    public void update() {
        if (exploded) return;
        if (keyH == null || gm == null) return;

        int nextX = x;
        int nextY = y;

        // Movement based on bomb-specific arrow keys
        if (keyH.bombUpPressed) {
            nextY -= speed;
            if (spriteUp != null) spriteCurrent = spriteUp;
        } else if (keyH.bombDownPressed) {
            nextY += speed;
            if (spriteDown != null) spriteCurrent = spriteDown;
        } else if (keyH.bombLeftPressed) {
            nextX -= speed;
            if (spriteLeft != null) spriteCurrent = spriteLeft;
        } else if (keyH.bombRightPressed) {
            nextX += speed;
            if (spriteRight != null) spriteCurrent = spriteRight;
        }

        // Collision checking (same logic style as hero/drone)
        if (canMoveTo(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            // Prevent clipping into walls
            snapToTileIfClose();
        }

        // Map bounds clamp
        if (x < 0) x = 0;
        if (y < 0) y = 0;

        int maxX = gm.tiles[0].length * gm.tileSize - gm.tileSize;
        int maxY = gm.tiles.length * gm.tileSize - gm.tileSize;

        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;

        // Update tile coordinates from center pixel
        int centerX = x + hitBox.x + hitBox.width / 2;
        int centerY = y + hitBox.y + hitBox.height / 2;
        tileCol = centerX / gm.tileSize;
        tileRow = centerY / gm.tileSize;
    }

    /**
     * Snaps the bomb back to the closest tile center if it is partially stuck
     * due to collision. This prevents jittering against walls.
     */
    private void snapToTileIfClose() {
        int centerX = x + hitBox.x + hitBox.width / 2;
        int centerY = y + hitBox.y + hitBox.height / 2;

        int col = centerX / gm.tileSize;
        int row = centerY / gm.tileSize;

        int desiredX = col * gm.tileSize - hitBox.x;
        int desiredY = row * gm.tileSize - hitBox.y;

        int dx = Math.abs(x - desiredX);
        int dy = Math.abs(y - desiredY);

        if (dx < gm.tileSize / 2) x = desiredX;
        if (dy < gm.tileSize / 2) y = desiredY;
    }

    /**
     * Checks whether the walking bomb can move to the pixel position (nextX,nextY).
     * <p>
     * Performs collision detection using the four corners of the bounding box.
     * Only tiles where {@link Tile#isWalkable()} is {@code true} are allowed.
     *
     * @return true if movement is allowed, false if blocked
     */
    private boolean canMoveTo(int nextX, int nextY) {
        int tileSize = gm.tileSize;

        int left = nextX + hitBox.x;
        int right = nextX + hitBox.x + hitBox.width - 1;
        int top = nextY + hitBox.y;
        int bottom = nextY + hitBox.y + hitBox.height - 1;

        int topRow = top / tileSize;
        int bottomRow = bottom / tileSize;
        int leftCol = left / tileSize;
        int rightCol = right / tileSize;

        // Out-of-bounds protection
        if (topRow < 0 || leftCol < 0 ||
            bottomRow >= gm.tiles.length || rightCol >= gm.tiles[0].length) {
            return false;
        }

        Tile t1 = gm.tiles[topRow][leftCol];
        Tile t2 = gm.tiles[topRow][rightCol];
        Tile t3 = gm.tiles[bottomRow][leftCol];
        Tile t4 = gm.tiles[bottomRow][rightCol];

        if (t1 == null || t2 == null || t3 == null || t4 == null) return false;

        // Movement is allowed ONLY if all tiles are walkable
        return t1.isWalkable() && t2.isWalkable() &&
               t3.isWalkable() && t4.isWalkable();
    }

    /**
     * Detonates the walking bomb, destroying tiles and damaging entities
     * in an extended cross-shaped radius of 2 tiles.
     * <p>
     * Explosion effects include:
     * <ul>
     *     <li>Destroying tiles at (center ± 1) and (center ± 2) along row/column.</li>
     *     <li>Damaging and marking drones in the affected cross.</li>
     *     <li>Damaging the hero if standing in the explosion radius.</li>
     * </ul>
     * After exploding, the walking bomb no longer updates or draws movement frames.
     */
    public void explode() {
        if (exploded) return;
        exploded = true;

        int centerR = tileRow;
        int centerC = tileCol;

        // Destroy center and extended cross (radius 2)
        gm.destroyTile(centerR, centerC);
        gm.destroyTile(centerR + 1, centerC);
        gm.destroyTile(centerR - 1, centerC);
        gm.destroyTile(centerR, centerC + 1);
        gm.destroyTile(centerR, centerC - 1);

        gm.destroyTile(centerR + 2, centerC);
        gm.destroyTile(centerR - 2, centerC);
        gm.destroyTile(centerR, centerC + 2);
        gm.destroyTile(centerR, centerC - 2);

        // Drone damage in cross radius 2
        for (int r = centerR - 2; r <= centerR + 2; r++) {
            for (int c = centerC - 2; c <= centerC + 2; c++) {
                if (r == centerR || c == centerC) {
                    gm.checkDroneDamageAt(r, c);
                }
            }
        }

        // Hero damage if standing in cross radius
        int hr = gm.hero.getTileRow();
        int hc = gm.hero.getTileCol();

        boolean heroHit =
                (hr == centerR && Math.abs(hc - centerC) <= 2) ||
                (hc == centerC && Math.abs(hr - centerR) <= 2);

        if (heroHit) {
            gm.handleHeroDeath();
        }
    }
}

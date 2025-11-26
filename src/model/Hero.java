package model;

import controller.KeyHandler;

import java.awt.*;

/**
 * Represents the player-controlled hero on the game board.
 * <p>
 * The hero has pixel coordinates, a hitbox, a limited number of hearts (lives),
 * movement speed, powerup flags, and methods for placing bombs or walking bombs.
 * The {@link GameModel} and {@link KeyHandler} are used to query world state
 * and player input respectively.
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
    public String direction = "left";
    private boolean hasWalkingBombPowerup = false;

    GameModel gm;
    KeyHandler keyH;

    public Rectangle hitBox = new Rectangle(32, 48, 32, 42);
    boolean collision = false;

    // Spawn position in tiles (columns/rows used when respawning)
    private int spawnCol = 4;
    private int spawnRow = 3;

    private boolean hasRadiusPowerup = false;

    // invulnerability
    private long invulnerableUntil = 0L; // System.currentTimeMillis() until which hero is invulnerable

    /**
     * Constructs a Hero placed at the given tile coordinates (x,y) and starting with the specified hearts.
     * <p>
     * Note: the constructor expects tile coordinates for x and y (tile column and tile row).
     * It converts them to pixel coordinates using {@code gm.tileSize}.
     *
     * @param x      initial tile column (will be multiplied by gm.tileSize)
     * @param y      initial tile row (will be multiplied by gm.tileSize)
     * @param hearts initial number of hearts/lives
     * @param gm     reference to the GameModel
     * @param keyH   reference to the KeyHandler for reading input
     */
    public Hero(int x, int y, int hearts, GameModel gm, KeyHandler keyH) {
        this.spawnCol = x;
        this.spawnRow = y;
        this.x = x * gm.tileSize;
        this.y = y * gm.tileSize;
        this.gm = gm;
        this.keyH = keyH;
    }

    /**
     * Returns the current horizontal pixel position of the hero.
     *
     * @return hero's pixel X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns whether the hero currently has the walking-bomb powerup.
     *
     * @return true if the hero has the walking bomb powerup
     */
    public boolean hasWalkingBombPowerup() {
        return hasWalkingBombPowerup;
    }

    /**
     * Sets whether the hero has the walking-bomb powerup.
     *
     * @param v true to grant the walking-bomb powerup; false to remove it
     */
    public void setHasWalkingBombPowerup(boolean v) {
        hasWalkingBombPowerup = v;
    }

    /**
     * Returns the current vertical pixel position of the hero.
     *
     * @return hero's pixel Y coordinate
     */
    public int getY() {
        return y;
    }

    // setters for external updates (network, remoteHero, etc)
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    /** @return the remaining hearts of the hero */
    public int getHearts() {
        return hearts;
    }

    /** Decrements the hero's hearts by one. */
    public void loseHeart() {
        hearts--;
    }

    /**
     * Updates hero state each frame: processes input, checks collisions, moves the hero,
     * and handles bomb/walking-bomb placement when the place button is pressed.
     *
     * Movement respects tile collisions using {@link CollisionChecker} from {@link GameModel}.
     * Bombs placed carry the hero's walking-bomb powerup state into the bomb instance.
     */
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

        // 1. If hero has Walking Bomb powerup -> spawn a WalkingBomb (ARROW KEY CONTROL)
        if (hasWalkingBombPowerup && (gm.walkingBombs == null || gm.walkingBombs.size() == 0)) {

            int centerX = getX() + gm.tileSize / 2;
            int centerY = getY() + gm.tileSize / 2;
            int spawnCol = centerX / gm.tileSize;
            int spawnRow = centerY / gm.tileSize;

            WalkingBomb wb = new WalkingBomb(spawnCol, spawnRow, gm, keyH);
            gm.walkingBombs.add(wb);

            System.out.println("Spawned walking bomb at " + spawnRow + "," + spawnCol);
        }

        // 2. Otherwise place a normal bomb → NOW WITH POWERED FLAG
        else {
            boolean hasActiveBomb = false;
            for (Bomba b : gm.bombs) {
                if (!b.exploded) {
                    hasActiveBomb = true;
                    break;
                }
            }

            if (!hasActiveBomb) {
                int centerX = getX() + gm.tileSize / 2;
                int centerY = getY() + gm.tileSize / 2;
                int brow = centerY / gm.tileSize;
                int bcol = centerX / gm.tileSize;

                // ⭐ PASS THE POWERUP STATE INTO THE BOMB
                Bomba b = new Bomba(brow, bcol, 3.0, gm, this.hasWalkingBombPowerup());
                gm.bombs.add(b);

                System.out.println("[H] Placed " + (b.isPowered() ? "POWERED" : "normal") +
                                " bomb at " + brow + "," + bcol);
            }
        }

        keyH.placePressed = false;
    }


    }

    // Helper getters for clarity (pixel vs tile coords)

    /**
     * Returns the raw pixel X coordinate (sprite top-left).
     *
     * @return pixel X
     */
    public int getPixelX() { return x; }

    /**
     * Returns the raw pixel Y coordinate (sprite top-left).
     *
     * @return pixel Y
     */
    public int getPixelY() { return y; }

    /**
     * Returns the hero center X pixel coordinate (based on hitBox).
     *
     * @return center pixel X
     */
    public int getCenterPixelX() {
        return this.getX() + this.hitBox.x + (this.hitBox.width / 2);
    }

    /**
     * Returns the hero center Y pixel coordinate (based on hitBox).
     *
     * @return center pixel Y
     */
    public int getCenterPixelY() {
        return this.getY() + this.hitBox.y + (this.hitBox.height / 2);
    }

    /**
     * Returns the tile column the hero currently occupies (based on center pixel).
     *
     * @return tile column index
     */
    public int getTileCol() {
        // column = center pixel X / tileSize
        return getCenterPixelX() / gm.tileSize;
    }

    /**
     * Returns the tile row the hero currently occupies (based on center pixel).
     *
     * @return tile row index
     */
    public int getTileRow() {
        // row = center pixel Y / tileSize
        return getCenterPixelY() / gm.tileSize;
    }

    /**
     * Returns the hero's movement speed in pixels per update.
     *
     * @return hero movement speed
     */
    public int getHeroSpeed() {
        return heroSpeed;
    }

    /**
     * Grants the hero invulnerability for the specified duration (seconds).
     *
     * @param seconds duration in seconds
     */
    public void makeInvulnerableForSeconds(double seconds) {
        invulnerableUntil = System.currentTimeMillis() + (long)(seconds * 1000.0);
    }

    /**
     * Returns whether the hero is currently invulnerable.
     *
     * @return true if invulnerable, false otherwise
     */
    public boolean isInvulnerable() {
        return System.currentTimeMillis() < invulnerableUntil;
    }

    /**
     * Respawns the hero at its spawn point and grants a short invulnerability window.
     */
    public void respawnAtSpawnWithInvulnerability() {
        this.x = spawnCol * gm.tileSize;
        this.y = spawnRow * gm.tileSize;
        makeInvulnerableForSeconds(3.0);
    }

    /**
     * Used by GamePanel to determine whether the hero should be drawn this frame.
     * <p>
     * When invulnerable the hero blinks: this method toggles drawing on a 200ms interval.
     *
     * @return true if the hero should be drawn this frame
     */
    public boolean isDrawnThisFrame() {
        if (!isInvulnerable()) return true;
        // blink while invulnerable: toggle based on time
        long t = System.currentTimeMillis();
        return ((t / 200) % 2) == 0; // blink every 200ms
    }

    // hasWalkingBombPowerup already exists

    /**
     * Returns whether the hero currently has the radius powerup (increases bomb radius).
     *
     * @return true if radius powerup is active
     */
    public boolean hasRadiusPowerup() {
        return hasRadiusPowerup;
    }

    /**
     * Sets the hero's radius powerup flag.
     *
     * @param v true to grant radius powerup; false to remove it
     */
    public void setHasRadiusPowerup(boolean v) {
        hasRadiusPowerup = v;
    }

    /** Adds a heart if below the maximum (3). */
    public void addHeart() {
        if (hearts < 3) hearts++;
    }

    /** Clears level-scoped powerups (called when changing levels). */
    public void clearLevelPowerups() {
        hasWalkingBombPowerup = false;
        hasRadiusPowerup = false;
    }
}

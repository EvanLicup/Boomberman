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
     * Constructs a main.Hero object and initializes its starting position and hearts.
     *
     * @param x      the initial horizontal pixel position (X). To convert tile column -> multiply by tileSize before passing
     * @param y      the initial vertical pixel position (Y). To convert tile row -> multiply by tileSize before passing
     * @param hearts the initial number of hearts (lives) the hero starts with
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
     * @return the hero's x-coordinate (pixel X)
     */
    public int getX() {
        return x;
    }
        public boolean hasWalkingBombPowerup() {
        return hasWalkingBombPowerup;
    }
    public void setHasWalkingBombPowerup(boolean v) {
        hasWalkingBombPowerup = v;
    }

    /**
     * Returns the current vertical pixel position of the hero.
     *
     * @return the hero's y-coordinate (pixel Y)
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

    // Invulnerability helpers
    public void makeInvulnerableForSeconds(double seconds) {
        invulnerableUntil = System.currentTimeMillis() + (long)(seconds * 1000.0);
    }

    public boolean isInvulnerable() {
        return System.currentTimeMillis() < invulnerableUntil;
    }

    // call to respawn at spawn point and set invulnerability
    public void respawnAtSpawnWithInvulnerability() {
        this.x = spawnCol * gm.tileSize;
        this.y = spawnRow * gm.tileSize;
        makeInvulnerableForSeconds(3.0);
    }

    // drawing helper used by GamePanel to decide whether to draw hero (for blink)
    public boolean isDrawnThisFrame() {
        if (!isInvulnerable()) return true;
        // blink while invulnerable: toggle based on time
        long t = System.currentTimeMillis();
        return ((t / 200) % 2) == 0; // blink every 200ms
    }
 
    // hasWalkingBombPowerup already exists

    public boolean hasRadiusPowerup() {
        return hasRadiusPowerup;
    }
    public void setHasRadiusPowerup(boolean v) {
        hasRadiusPowerup = v;
    }

    // add heart if under max (3)
    public void addHeart() {
        if (hearts < 3) hearts++;
    }

    // optionally a convenience to clear level-lifetime powerups on level change
    public void clearLevelPowerups() {
        hasWalkingBombPowerup = false;
        hasRadiusPowerup = false;
    }
}

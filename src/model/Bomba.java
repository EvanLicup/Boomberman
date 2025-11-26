package model;

import java.util.ArrayList;

/**
 * Represents a bomb placed on the game board. 
 * <p>
 * A {@code Bomba} tracks its position, countdown timer, explosion radius,
 * and whether it was placed while a powerup was active. When its timer
 * reaches zero, it explodes and applies damage to tiles, drones, and the hero.
 */
public class Bomba {

    /** The row where this bomb is placed. */
    private int row;

    /** The column where this bomb is placed. */
    private int col;

    /** Time remaining until the bomb explodes. */
    private double timeTilExplosion;

    /** Indicates whether the bomb has already exploded. */
    public boolean exploded = false;

    /** Reference to the {@link GameModel} managing the game state. */
    private GameModel gm;

    /** Whether the bomb was placed while the hero had a powerup. */
    private boolean powered = false;

    /**
     * Creates a new bomb with the given parameters.
     *
     * @param row the row where the bomb is placed
     * @param col the column where the bomb is placed
     * @param timeTilExplosion time remaining until explosion
     * @param gm reference to the game model
     * @param powered whether the bomb was placed with a radius powerup active
     */
    public Bomba(int row, int col, double timeTilExplosion, GameModel gm, boolean powered) {
        this.row = row;
        this.col = col;
        this.gm = gm;
        this.timeTilExplosion = timeTilExplosion;
        this.powered = powered;
    }

    /**
     * Convenience constructor for non-powered bombs.
     *
     * @param row the row where the bomb is placed
     * @param col the column where the bomb is placed
     * @param timeTilExplosion time remaining until explosion
     * @param gm reference to the game model
     */
    public Bomba(int row, int col, double timeTilExplosion, GameModel gm) {
        this(row, col, timeTilExplosion, gm, false);
    }

    /**
     * Causes the bomb to explode, damaging nearby tiles, drones, and the hero.
     * <p>
     * Explosion rules:
     * <ul>
     *   <li>Explosion radius is determined by whether the hero currently has a powerup.</li>
     *   <li>Calls {@code explodeAt()} in {@link GameModel} if available.</li>
     *   <li>If unavailable, handles explosion manually (cross pattern).</li>
     *   <li>Stops at indestructible tiles ('I').</li>
     *   <li>Destroys destructible tiles ('D') then stops propagation.</li>
     *   <li>Damages drones and awards score.</li>
     * </ul>
     */
    public void explode() {
        // mark exploded so update/cleanup logic won't process this bomb again
        exploded = true;

        // determine radius: base 1, but hero may have radius powerup (radius = 2)
        // determine radius using the bomb's own powered flag (set when the bomb was placed)
        int radius = gm.hero.hasRadiusPowerup() ? 2 : 1;



        System.out.println("Bomba exploded at row=" + row + ", col=" + col + " with radius=" + radius);

        // First attempt: if GameModel exposes explodeAt(row,col,radius) prefer that so single place handles explosion
        try {
            // reflection-safe call: if method exists, call it (keeps compatibility with your GameModel design)
            // NOTE: you can remove this try-block if you add explodeAt to GameModel; it's safe even if method absent.
            gm.getClass().getMethod("explodeAt", int.class, int.class, int.class).invoke(gm, row, col, radius);
            return;
        } catch (NoSuchMethodException nsme) {
            // method not present — we'll fall back to internal implementation below
        } catch (Exception e) {
            // other reflection error — fallback as well
            System.err.println("explodeAt invocation failed, falling back to local explode implementation: " + e.getMessage());
        }

        // --- fallback explosion implementation (behaviour matches requested rules) ---
        if (gm == null) return;

        // HERO DAMAGE: hero is hit if on same row within radius cols OR same col within radius rows
        try {
            int heroRow = gm.hero.getTileRow();
            int heroCol = gm.hero.getTileCol();
            boolean heroHit = (heroRow == row && Math.abs(heroCol - col) <= radius)
                           || (heroCol == col && Math.abs(heroRow - row) <= radius);
            if (heroHit) {
                gm.handleHeroDeath();
            }
        } catch (Exception e) {
            // ignore hero damage errors
        }

        // destroy center and propagate in 4 directions, obeying rules:
        // - stop when encountering 'I' (indestructible) (tile not affected)
        // - if encounter 'D' destroy it and then stop propagation in that direction
        destroyTileRespectingRules(row, col);

        // UP
        for (int d = 1; d <= radius; d++) {
            int r = row - d;
            int c = col;
            if (!inBounds(r, c)) break;
            char t = gm.tiles[r][c].getType();
            if (t == 'I') break;
            destroyTileRespectingRules(r, c);
            if (t == 'D') break;
        }

        // DOWN
        for (int d = 1; d <= radius; d++) {
            int r = row + d;
            int c = col;
            if (!inBounds(r, c)) break;
            char t = gm.tiles[r][c].getType();
            if (t == 'I') break;
            destroyTileRespectingRules(r, c);
            if (t == 'D') break;
        }

        // LEFT
        for (int d = 1; d <= radius; d++) {
            int r = row;
            int c = col - d;
            if (!inBounds(r, c)) break;
            char t = gm.tiles[r][c].getType();
            if (t == 'I') break;
            destroyTileRespectingRules(r, c);
            if (t == 'D') break;
        }

        // RIGHT
        for (int d = 1; d <= radius; d++) {
            int r = row;
            int c = col + d;
            if (!inBounds(r, c)) break;
            char t = gm.tiles[r][c].getType();
            if (t == 'I') break;
            destroyTileRespectingRules(r, c);
            if (t == 'D') break;
        }

        // Damage drones in the cross up to radius (mark them dead, award score)
        try {
            int killed = 0;
            if (gm.drones != null) {
                ArrayList<drone> snapshot = new ArrayList<>(gm.drones);
                for (drone d : snapshot) {
                    int dr = d.getTileRow();
                    int dc = d.getTileCol();
                    boolean inCross = (dr == row && Math.abs(dc - col) <= radius) || (dc == col && Math.abs(dr - row) <= radius);
                    if (inCross && !d.isDead()) {
                        d.markAsDead();
                        killed++;
                        gm.score += 35;
                    }
                }
            }
            if (killed > 0) {
                System.out.println("Killed drones: " + killed + " (score now " + gm.score + ")");
            }
        } catch (Exception e) {
            // ignore drone marking errors
        }
    }

    /**
     * Checks whether the specified row and column are within the game board bounds.
     *
     * @param r the row to check
     * @param c the column to check
     * @return true if inside bounds, false otherwise
     */
    private boolean inBounds(int r, int c) {
        return gm != null && r >= 0 && r < gm.tiles.length && c >= 0 && c < gm.tiles[0].length;
    }

    /**
     * Attempts to destroy a tile at the given coordinates, respecting destruction rules:
     * <ul>
     *   <li>Indestructible tiles ('I') are not destroyed.</li>
     *   <<li>Destructible tiles ('D') are removed.</li>
     *   <li>Delegates to {@code gm.destroyTile()} if possible.</li>
     *   <li>If destruction fails, the tile becomes a walkable tile as fallback.</li>
     * </ul>
     *
     * @param r the row of the tile
     * @param c the column of the tile
     */
    private void destroyTileRespectingRules(int r, int c) {
        if (!inBounds(r, c)) return;
        try {
            char t = gm.tiles[r][c].getType();
            if (t == 'I') return; // never call destroy on indestructible
            // delegate to GameModel so counters/powerups/score are handled consistently
            gm.destroyTile(r, c);
        } catch (Exception e) {
            // fallback: if destroyTile throws/absent, try to mutate map directly (defensive)
            try {
                if (inBounds(r, c) && gm.tiles[r][c].getType() == 'D') {
                    gm.tiles[r][c] = new WalkableTile(r, c);
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Reduces the timer until explosion and triggers an explosion when it reaches zero.
     *
     * @param delta the amount of time to subtract
     */
    public void decreaseTime (double delta) {
        if (exploded == true) return;
        timeTilExplosion -= delta;
        if (timeTilExplosion <= 0) {
            explode();
        }
    }

    /**
     * Returns the row where the bomb is placed.
     *
     * @return the bomb's row
     */
    public int getRow() { return row; }

    /**
     * Returns the column where the bomb is placed.
     *
     * @return the bomb's column
     */
    public int getCol() { return col; }

    /**
     * Returns whether the bomb was placed while the player had a radius powerup.
     *
     * @return true if powered, false otherwise
     */
    public boolean isPowered() { return powered; }
}

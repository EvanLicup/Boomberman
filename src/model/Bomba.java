package model;

import java.util.ArrayList;

public class Bomba {
    private int row;
    private int col;
    private double timeTilExplosion;
    public boolean exploded = false;
    private GameModel gm;

    // new: whether this bomb is "powered" (placed while powerup active)
    private boolean powered = false;

    // primary constructor (powered flag)
    public Bomba(int row, int col, double timeTilExplosion, GameModel gm, boolean powered) {
        this.row = row;
        this.col = col;
        this.gm = gm;
        this.timeTilExplosion = timeTilExplosion;
        this.powered = powered;
    }

    // convenience/backwards-compatible constructor (no powered flag) -> not powered
    public Bomba(int row, int col, double timeTilExplosion, GameModel gm) {
        this(row, col, timeTilExplosion, gm, false);
    }

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

    private boolean inBounds(int r, int c) {
        return gm != null && r >= 0 && r < gm.tiles.length && c >= 0 && c < gm.tiles[0].length;
    }

    // call gm.destroyTile but guard against indestructible tiles.
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

    public void decreaseTime (double delta) {
        if (exploded == true) return;
        timeTilExplosion -= delta;
        if (timeTilExplosion <= 0) {
            explode();
        }
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    // new getter for powered
    public boolean isPowered() { return powered; }
}

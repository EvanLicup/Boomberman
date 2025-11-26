package model;

import controller.KeyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Primary game model that holds the entire game state and logic.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>maintaining the tile map and entities (hero, drones, bombs, powerups),</li>
 *   <li>level setup and deterministic destructible placement,</li>
 *   <li>handling bomb explosions and drone damage,</li>
 *   <li>managing hero lives, respawn, and game-over state,</li>
 *   <li>progression between levels and spawning exit/powerups/drones.</li>
 * </ul>
 *
 * The model is intentionally defensive and attempts to delegate some actions
 * (e.g., tile destruction) so rendering and scoring are kept consistent.
 */
public class GameModel {
    public CollisionChecker cChecker;
    public ArrayList<Bomba> bombs = new ArrayList<>();
    Tile tiles[][];
    final int originalTileSize = 32;
    final int scale = 3;
    KeyHandler keyH;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public Hero hero;

    // powerup & walking bomb collections
    public ArrayList<PowerUp> powerups = new ArrayList<>();
    public ArrayList<WalkingBomb> walkingBombs = new ArrayList<>();
    private int powerupsToPlace = 0;
    private int powerupsPlaced = 0;

    // NEW: set true when player finishes the final level
    public boolean finishedAllLevels = false;

    // Level / progression fields
    public int level = 1;
    public int maxLevel = 5;
    public int destroyedCratesCount = 0;
    public int requiredCratesToUnlockExit = 12; // will be set per-level in startLevel()

    // Exit location (-1 = none)
    public int exitRow = -1;
    public int exitCol = -1;

    public String message = null;
    private long messageUntil = 0L; // System.currentTimeMillis() until which message is shown
    private static final long MESSAGE_MS = 5000L; // show for 5 seconds

    // Score
    public int score = 0;

    // Game/lives state
    public boolean gameOver = false; // true when hero has 0 hearts

    private static final java.util.Set<String> FORBIDDEN_DESTRUCT = java.util.Set.of(
        "5,3",  // (row=5, col=3)  <- {3,5}
        "3,13", // (row=3, col=13) <- {13,3}
        "7,7",  // (row=7, col=7)  <- {7,7}
        "5,11", // (row=5, col=11) <- {11,5}
        "5,9",  // (row=5, col=9)  <- {9,5}
        "9,15"  // (row=9, col=15) <- {15,9}
    );

    /**
     * Shows an onscreen/debug message for a short duration.
     *
     * @param txt the text to show; ignored if null
     */
    public void showMessage(String txt) {
        if (txt == null) return;
        this.message = txt;
        this.messageUntil = System.currentTimeMillis() + MESSAGE_MS;
        System.out.println("MESSAGE: " + txt);
    }

    /**
     * Constructs the GameModel with the provided KeyHandler.
     * <p>
     * This initializes the tile map from {@link #inputMap}, the hero,
     * collision checker, and immediately starts the level setup.
     *
     * @param keyH the KeyHandler used to read player input
     */
    public GameModel(KeyHandler keyH) {
        this.keyH = keyH;
        this.tiles = new Tile[inputMap.length][inputMap[0].length];
        this.hero = new Hero(4,3,3,this, keyH);
        this.cChecker = new CollisionChecker(this);
        initializeTiles();

        // spawn level entities (drones) and set required crates
        startLevel(this.level);
    }

    // <-- user-provided, exact inputMap used
    char[][] inputMap = {
            {'1','T','T','T','T','T','T','T','T','T','T','T','T','T','T','T','2'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I','D','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D','D','R'},
            {'3','B','B','B','B','B','B','B','B','B','B','B','B','B','B','B','4'}
    };

    /**
     * Initializes the tiles[][] array from the original {@link #inputMap}.
     * <p>
     * Converts characters into specific Tile subclasses (IndestructibleTile,
     * DestructibleTile, WalkableTile, BarrierTile) and preserves the exact
     * input map layout.
     */
    public void initializeTiles() {

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                char c = inputMap[i][j];

                if (c == 'I') {
                    tiles[i][j] = new IndestructibleTile(i, j);
                }
                else if (c == 'D') {
                    tiles[i][j] = new DestructibleTile(i, j);
                }
                else if (c == ' ') {
                    tiles[i][j] = new WalkableTile(i, j);
                }
                else if (c == 'B') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'L') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'R') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'T') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '1') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '2') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '3') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '4') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else {
                    System.out.println("Unknown char at (" + i + "," + j + ")");
                    tiles[i][j] = null;
                }
            }
        }
    }



    /**
     * Updates the entire game model each frame tick.
     * <p>
     * The update includes hero updates, drone updates and collision checks,
     * walking bomb updates, bomb countdowns, powerup pickup handling, and
     * housekeeping such as removing exploded bombs.
     *
     * @param delta time elapsed since last update (game time unit)
     */
    public void update(double delta) {
    
        if (gameOver || finishedAllLevels) {
    return; // stop hero, drones, bombs, everything
    }
    // update player
    hero.update();
    int hr = hero.getTileRow();
    int hc = hero.getTileCol();
    if (message != null && System.currentTimeMillis() > messageUntil) {
        message = null;
    }
    // update drones (if any)
    if (drones != null) {
        for (int i = 0; i < drones.size(); i++) {
            try {
                drones.get(i).update();
            } catch (Exception e) {
                // robust: if a drone throws, keep going
                e.printStackTrace();
            }
        }

        // AFTER updating drone positions, check collisions with hero (tile-based)
        // Only ALIVE drones can kill the hero. Dead drones are decorative and ignored.
        if (hero != null && !gameOver) {

            for (int i = 0; i < drones.size(); i++) {
                try {
                    drone d = drones.get(i);
                    if (d == null) continue;
                    if (d.isDead()) continue; // skip dead (non-lethal) drones

                    int dr = d.getTileRow();
                    int dc = d.getTileCol();

                    if (dr == hr && dc == hc) {
                        // delegate to centralized handler (it checks invulnerability and sets gameOver)
                        handleHeroDeath();
                        break; // handle at most one collision this frame
                    }
                } catch (Exception e) {
                    // ignore single drone error and continue
                }
            }
        }
    }

    // update walking bombs (they are controllable)
    if (walkingBombs != null) {
        for (int i = 0; i < walkingBombs.size(); i++) {
            try {
                walkingBombs.get(i).update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // remove exploded walking bombs (they do their own explosion in explode())
        walkingBombs.removeIf(wb -> wb.isExploded());
    }

        // handle walking-bomb detonation by pressing J
        if (keyH != null && keyH.detonatePressed) {
            if (walkingBombs != null) {
                for (WalkingBomb wb : new ArrayList<>(walkingBombs)) {
                    if (wb != null && !wb.isExploded()) {
                        wb.explode();
                        // remove walking bomb - explode() will already handle destroying tiles, drones, hero damage
                    }
                }
                // remove exploded ones (walkingBombs.removeIf(wb -> wb.isExploded()) happens later)
            }
            // consume the detonate press so it's not repeated (Game loop will set it false after handling)
            keyH.detonatePressed = false;
            // If the powerup should be consumed on detonation:
            if (hero != null) hero.setHasWalkingBombPowerup(false);
        }

    // powerup pickup check: if hero steps on a powerup tile, grant it
    if (powerups != null && hero != null) {
        for (PowerUp pu : powerups) {
            if (pu == null || pu.isPicked()) continue;

            if (pu.row == hr && pu.col == hc) {
                pu.pick();

                // show instruction once
                if (!pu.isInstructionShown()) {
                    showMessage(pu.getInstructionText());
                    pu.setInstructionShown(true);
                }

                switch (pu.type) {
                    case WALKING:
                        hero.setHasWalkingBombPowerup(true);
                        showMessage("Walking Bomb acquired! Press H to spawn it, use arrow keys to move, press J to detonate.");
                        break;
                    case RADIUS:
                        hero.setHasRadiusPowerup(true);
                        showMessage("Power+1: Your bombs now reach 2 tiles!");
                        break;
                    case EXTRA_LIFE:
                        if (hero.getHearts() < 3) {
                            hero.addHeart();
                            showMessage("You gained +1 life!");
                        } else {
                            score += 100;
                            showMessage("+100 points (max lives).");
                        }
                        break;
                }

                System.out.println("Hero picked powerup at " + pu.row + "," + pu.col + " type=" + pu.type);
            }
        }
        // remove picked powerups from list so they disappear visually
        powerups.removeIf(p -> p.isPicked());
    }
    // update bombs
    for (int i = 0; i < bombs.size(); i++) {
        Bomba b = bombs.get(i);
        b.decreaseTime(delta);
    }

    // remove exploded bombs (keeps bombs list clean)
    bombs.removeIf(b -> b.exploded);

    // NOTE: do NOT remove dead drones here — they should persist until level end/exit.
    }



    /**
     * Handles hero death: decrement hearts, respawn if possible, or end the game.
     * <p>
     * This method checks invulnerability and delegates respawn or game over logic.
     */
        public void handleHeroDeath() {
            if (hero == null) return;
            if (hero.isInvulnerable()) {
                // already invulnerable — no effect
                return;
            }

            hero.loseHeart();
            System.out.println("Hero died. Hearts remaining: " + hero.getHearts());

            if (hero.getHearts() <= 0) {
                // game over, do not respawn
                gameOver = true;
                System.out.println("GAME OVER (score: " + score + ")");
            } else {
                // respawn with invulnerability
                hero.respawnAtSpawnWithInvulnerability();
            }
        }
    /**
     * Destroys a destructible tile at the specified coordinates.
     * If a destructible tile is destroyed, handleTileDestroyed is called.
     *
     * @param row the row index
     * @param col the col index
     */
    /**
     * Destroys a destructible tile at the specified coordinates.
     * If a destructible tile is destroyed, handleTileDestroyed is called.
     * ALWAYS spawns a powerup (when powerupsPlaced < powerupsToPlace).
     *
     * @param row the row index of the tile to destroy
     * @param col the column index of the tile to destroy
     */
public void destroyTile(int row, int col) {
    if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[row].length) {
        if (tiles[row][col].getType() == 'D') {
            // convert tile to walkable and notify model
            tiles[row][col] = new WalkableTile(row, col);
            handleTileDestroyed(row, col);

            // If we still need to place powerups this level, ALWAYS spawn one here
            if (powerupsPlaced < powerupsToPlace) {
                java.util.Random r = new java.util.Random();
                // pick which powerup randomly or by weighted chance (kept previous weights)
                PowerUp.Type chosen;
                int pick = r.nextInt(100);
                if (pick < 45) {
                    chosen = PowerUp.Type.WALKING; // most common
                } else if (pick < 80) {
                    chosen = PowerUp.Type.RADIUS;
                } else {
                    chosen = PowerUp.Type.EXTRA_LIFE;
                }
                PowerUp pu = new PowerUp(row, col, chosen);
                powerups.add(pu);
                powerupsPlaced++;
                System.out.println("Powerup spawned (" + chosen + ") at " + row + "," + col);
            }
        }
    }
    System.out.println("Destroyed tile " + row + "," + col);
}


    /**
     * Called when a destructible tile is destroyed.
     * increments counters, spawns exit if threshold reached, awards points.
     *
     * @param row the row index that was destroyed
     * @param col the column index that was destroyed
     */
    public void handleTileDestroyed(int row, int col) {
        destroyedCratesCount++;
        score += 15; // each crate = 15 points
        System.out.println("Crates destroyed: " + destroyedCratesCount + "/" + requiredCratesToUnlockExit);

        if (destroyedCratesCount >= requiredCratesToUnlockExit && exitRow == -1) {
            // place exit on the tile that was just destroyed
            spawnExitAt(row, col);
        }
    }

    /**
     * Spawns an exit at the given tile coordinates.
     *
     * @param row the row index for the exit
     * @param col the column index for the exit
     */
    public void spawnExitAt(int row, int col) {
        exitRow = row;
        exitCol = col;
        System.out.println("Exit spawned at: " + exitRow + "," + exitCol);
    }

    /**
     * Called when the hero reaches the exit tile — advances level if present.
     */
    public void onHeroReachExit() {
        if (exitRow == -1) return;
        if (hero.getTileRow() == exitRow && hero.getTileCol() == exitCol) {
            // advance level
            advanceLevel();
        }
    }

    /**
     * Advances the player to the next level or marks completion if final level reached.
     * <p>
     * This method resets necessary counters, clears bombs and drones, respawns the hero,
     * and calls {@link #startLevel(int)} for the new level.
     */
    public void advanceLevel() {
        // If already finished, do nothing
        if (finishedAllLevels) return;

        if (level >= maxLevel) {
            // player completed the final level
            finishedAllLevels = true;
            System.out.println("You've finished the final level! Score: " + score);
            // Do not automatically wrap to level 1; leave map as-is and stop progression.
            return;
        } else {
            level++;
        }

        // reset counters and exit
        destroyedCratesCount = 0;
        exitRow = -1;
        exitCol = -1;

        // clear bombs and drones
        bombs.clear();
        drones.clear();

        // reset hero to spawn and invulnerability for level-start
        hero.respawnAtSpawnWithInvulnerability();

        // setup new level
        startLevel(level);
    }


    /**
     * Destroys drones that are inside explosion cross of (row,col).
     * Marks drones as dead (so death sprite can display) and awards points per drone destroyed.
     * Returns number of drones marked.
     *
     * @param row the center row of the explosion
     * @param col the center column of the explosion
     * @return number of drones marked as dead
     */
    public int checkDroneDamageAt(int row, int col) {
        int marked = 0;
        if (drones == null || drones.size() == 0) return marked;

        ArrayList<drone> toMark = new ArrayList<>();
        for (drone d : drones) {
            int dr = d.getTileRow();
            int dc = d.getTileCol();
            if ((dr == row && dc == col) ||
                (dr == row + 1 && dc == col) ||
                (dr == row - 1 && dc == col) ||
                (dr == row && dc == col + 1) ||
                (dr == row && dc == col - 1)) {
                toMark.add(d);
            }
        }
        for (drone d : toMark) {
            // mark as dead so it displays death sprite for a moment and then removed later
            d.markAsDead();
            marked++;
            score += 35; // drone destroyed = 35 points
            System.out.println("Drone killed (marked) at tile " + d.getTileRow() + "," + d.getTileCol());
        }
        return marked;
    }

    /**
     * Executes an explosion at the given center and radius following destruction rules.
     * <p>
     * Rules:
     * <ul>
     *   <li>Center is processed unless out of bounds or indestructible.</li>
     *   <li>Propagation stops at indestructible tiles ('I').</li>
     *   <li>On encountering a destructible tile ('D') that direction destroys it and stops.</li>
     *   <li>Damages drones and the hero if inside the explosion cross.</li>
     * </ul>
     *
     * @param centerR center row for explosion
     * @param centerC center column for explosion
     * @param radius number of tiles to propagate in each cardinal direction
     */
    public void explodeAt(int centerR, int centerC, int radius) {
        // center always processed (if within bounds)
        if (centerR >= 0 && centerR < tiles.length && centerC >= 0 && centerC < tiles[0].length) {
            if (tiles[centerR][centerC].getType() != 'I') {
                destroyTile(centerR, centerC);
            }
        }

        // four cardinal directions
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        for (int[] d : dirs) {
            boolean destroyedDestructible = false; // ensure only one D destroyed per direction
            for (int dist = 1; dist <= radius; dist++) {
                int r = centerR + d[0] * dist;
                int c = centerC + d[1] * dist;

                // bounds check
                if (r < 0 || r >= tiles.length || c < 0 || c >= tiles[0].length) break;

                char t = tiles[r][c].getType();
                if (t == 'I') {
                    // indestructible -> explosion stops; tile unaffected
                    break;
                } else if (t == 'D') {
                    if (!destroyedDestructible) {
                        // destroy this one destructible tile and then stop further in this direction
                        destroyTile(r, c);
                        destroyedDestructible = true;
                        break;
                    } else {
                        // already destroyed one destructible in this direction -> stop
                        break;
                    }
                } else {
                    // walkable or other tile that can be affected: destroy (makes walkable)
                    destroyTile(r, c);
                    // continue to next distance
                }
            }
        }

        // damage drones in the cross within radius (including center)
        for (int dr = centerR - radius; dr <= centerR + radius; dr++) {
            for (int dc = centerC - radius; dc <= centerC + radius; dc++) {
                if (dr == centerR || dc == centerC) {
                    gmCheckDroneDamageSafe(dr, dc);
                }
            }
        }

        // hero damage: check if hero is in cross
        int hr = hero.getTileRow();
        int hc = hero.getTileCol();
        boolean heroHit = (hr == centerR && Math.abs(hc - centerC) <= radius) || (hc == centerC && Math.abs(hr - centerR) <= radius);
        if (heroHit) {
            handleHeroDeath();
        }
    }

    // wrapper to call checkDroneDamageAt safely (avoids confusion between gm and this)
    private void gmCheckDroneDamageSafe(int r, int c) {
        checkDroneDamageAt(r, c);
    }
    // collection for enemy drones (bots)
    public ArrayList<drone> drones = new ArrayList<>();

    /**
     * Convenience helper to spawn a drone at a given tile coordinate (col, row).
     * Tile coordinates are expected (col = column index, row = row index).
     * This method will only add a drone if the tile is valid/walkable and not too close to the hero.
     *
     * @param col tile column to spawn at
     * @param row tile row to spawn at
     */
    public void spawnDrone(int col, int row) {
        // safety checks
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) return;
        if (isTileIndestructible(row, col)) return;
        if (isWithinOneCardinalTileOfHero(row, col)) return;
        // convert col/row to drone constructor (drone expects tileCol, tileRow)
        drone d = new drone(col, row, this);
        drones.add(d);
    }

    /**
     * Starts the specified level: reinitializes map, places destructible tiles deterministically,
     * sets required crates thresholds, and spawns drones/powerups for the level.
     *
     * @param level the level index to start (1..maxLevel)
     */
    public void startLevel(int level) {

    // REINITIALIZE the tilemap from the original inputMap so previous play changes don't carry over
    initializeTiles();

    // set required crates exactly as requested (user-specified)
    switch (level) {
        case 1:
            requiredCratesToUnlockExit = 12;
            break;
        case 2:
            requiredCratesToUnlockExit = 21;
            break;
        case 3:
            requiredCratesToUnlockExit = 25;
            break;
        case 4:
            requiredCratesToUnlockExit = 32;
            break;
        case 5:
        default:
            requiredCratesToUnlockExit = 41;
            break;
    }

    // Reset counters and exit markers
    destroyedCratesCount = 0;
    exitRow = -1;
    exitCol = -1;

    // reset powerup/walking-bomb state for this level
    if (powerups == null) powerups = new ArrayList<>();
    else powerups.clear();
    if (walkingBombs == null) walkingBombs = new ArrayList<>();
    else walkingBombs.clear();
    powerupsPlaced = 0;
    powerupsToPlace = (level <= 4) ? 22 : 23;

    hero.clearLevelPowerups();

    // Determine desired total destructible tiles for this level using deterministic RNG (NEW RANGES)
    int desiredTotal = determineDestructibleTotalForLevel(level);

    // Apply destructible/walkable assignment deterministically
    setDestructibleTilesDeterministic(level, desiredTotal);

    // simple per-level drone spawn points & counts (you can tune)
// --- per-level spawn definitions ---
// normalSpawnPoints and fastSpawnPoints are set per-level below (col,row)
int[][] normalSpawnPoints;
int[][] fastSpawnPoints;

switch (level) {
    case 1:
        normalSpawnPoints = new int[][]{{3,5}};       // normal drone at (3,5)
        fastSpawnPoints   = new int[][]{{13,3}};      // fast drone at (13,3)
        break;
    case 2:
        normalSpawnPoints = new int[][]{{3,5},{13,3}};
        fastSpawnPoints   = new int[][]{{7,7}};
        break;
    case 3:
        normalSpawnPoints = new int[][]{{3,5},{13,3},{7,7}};
        fastSpawnPoints   = new int[][]{{11,5}};
        break;
    case 4:
        normalSpawnPoints = new int[][]{{3,5},{13,3},{7,7},{11,5}};
        fastSpawnPoints   = new int[][]{{9,5}};
        break;
    case 5:
    default:
        normalSpawnPoints = new int[][]{{3,5},{13,3},{7,7},{11,5},{9,5}};
        fastSpawnPoints   = new int[][]{{15,9}};
        break;
}

// Spawn drones (clear then add normal + fast with fallback)
drones.clear();

// spawn normal drones
for (int[] p : normalSpawnPoints) {
    int desiredCol = p[0];
    int desiredRow = p[1];

    if (isTileIndestructible(desiredRow, desiredCol) || isWithinOneCardinalTileOfHero(desiredRow, desiredCol)) {
        int[] alt = findNearestValidSpawn(desiredRow, desiredCol);
        if (alt != null) {
            spawnDrone(alt[1], alt[0]); // spawnDrone expects (col,row)
        } else {
            // no valid alternative found; skip
        }
    } else {
        spawnDrone(desiredCol, desiredRow);
    }
}

// spawn fast drones
for (int[] p : fastSpawnPoints) {
    int desiredCol = p[0];
    int desiredRow = p[1];

    if (isTileIndestructible(desiredRow, desiredCol) || isWithinOneCardinalTileOfHero(desiredRow, desiredCol)) {
        int[] alt = findNearestValidSpawn(desiredRow, desiredCol);
        if (alt != null) {
            spawnFastDrone(alt[1], alt[0]); // spawnFastDrone expects (col,row)
        } else {
            // no valid alternative found; skip
        }
    } else {
        spawnFastDrone(desiredCol, desiredRow);
    }
}
    // ensure hero is at spawn and invulnerable for level start
    hero.respawnAtSpawnWithInvulnerability();

    // Debug: dump destructible coords + drone spawns so user can verify placements
    debugDumpLevelSetup(desiredTotal);

    System.out.println("Started level " + level + ". Required crates: " + requiredCratesToUnlockExit
            + ". Drones: " + drones.size() + ". Destructible tiles total: " + desiredTotal
            + ". Powerups to place: " + powerupsToPlace);
}

public void spawnFastDrone(int col, int row) {
    if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) return;
    if (isTileIndestructible(row, col)) return;
    if (isWithinOneCardinalTileOfHero(row, col)) return;

    FastDrone fd = new FastDrone(col, row, this);
    drones.add(fd);
}
    /**
     * Determine the desired number of destructible tiles for the level using deterministic randomness.
     * NEW ranges (user request):
     * Level 1: 16..22
     * Level 2: 22..27
     * Level 3: 28..36
     * Level 4: 36..43
     * Level 5: 44..50
     *
     * The chosen number is deterministic for a given level (Random seeded with level).
     *
     * @param level level to compute the range for
     * @return the chosen number of destructible tiles for the level
     */
    private int determineDestructibleTotalForLevel(int level) {
        int min, max;
        switch (level) {
            case 1:
                min = 16; max = 22; break;
            case 2:
                min = 22; max = 27; break;
            case 3:
                min = 28; max = 36; break;
            case 4:
                min = 36; max = 43; break;
            case 5:
            default:
                min = 44; max = 50; break;
        }
        Random rand = new Random(level); // deterministic seed per level
        if (max <= min) return min;
        return min + rand.nextInt(max - min + 1);
    }

    /**
     * Deterministically selects which tiles will be destructible for the level.
     * Uses Random(level) as seed so layout is identical every run for a given level.
     *
     * - Candidates are tiles that are currently Walkable or Destructible (we kept this after reinitialization).
     * - We exclude tiles that are within one cardinal tile of the hero (hero tile and its up/down/left/right neighbors).
     * - We also exclude indestructible tiles (type 'I').
     *
     * @param level the level index (used as RNG seed)
     * @param desiredTotal number of tiles to mark as destructible
     */
    private void setDestructibleTilesDeterministic(int level, int desiredTotal) {
        List<int[]> candidates = new ArrayList<>();

        // build hero protection coords
        int heroRow = hero.getTileRow();
        int heroCol = hero.getTileCol();

        // Build candidate list: tiles that are currently Walkable or Destructible and not indestructible
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[r].length; c++) {
                char t = tiles[r][c].getType();
                // allow converting walkable or existing destructible tiles
                if (t == ' ' || t == 'D') {
                    // skip hero spawn vicinity (tile itself and its 4 cardinal neighbors)
                    if (Math.abs(r - heroRow) + Math.abs(c - heroCol) <= 1) continue;
                    // also skip indestructible tiles just in case
                    if (isTileIndestructible(r, c)) continue;
                    // skip forbidden destructible locations (convert from col,row request to row,col)
                    String key = r + "," + c;
                    if (FORBIDDEN_DESTRUCT.contains(key)) continue;

                    candidates.add(new int[]{r, c});
                }
            }
        }

        // If desiredTotal is larger than candidate count, clamp it
        if (desiredTotal > candidates.size()) {
            desiredTotal = candidates.size();
        }

        // Shuffle deterministically using level as seed
        Random rand = new Random(level);
        Collections.shuffle(candidates, rand);

        // First desiredTotal candidates become destructible; the rest become walkable
        for (int i = 0; i < candidates.size(); i++) {
            int[] pos = candidates.get(i);
            int r = pos[0];
            int c = pos[1];
            if (i < desiredTotal) {
                tiles[r][c] = new DestructibleTile(r, c);
            } else {
                tiles[r][c] = new WalkableTile(r, c);
            }
        }

        // Defensive: ensure forbidden coords are walkable (in case inputMap earlier had a 'D' there)
        for (String s : FORBIDDEN_DESTRUCT) {
            String[] parts = s.split(",");
            int fr = Integer.parseInt(parts[0]);
            int fc = Integer.parseInt(parts[1]);
            if (fr >= 0 && fr < tiles.length && fc >= 0 && fc < tiles[0].length) {
                tiles[fr][fc] = new WalkableTile(fr, fc);
            }
        }
    }

    /**
     * Return true if the tile (row,col) is explicitly indestructible 'I' or otherwise considered not usable.
     *
     * @param row tile row index
     * @param col tile column index
     * @return true if tile is indestructible or out of bounds
     */
    private boolean isTileIndestructible(int row, int col) {
        if (row < 0 || row >= tiles.length || col < 0 || col >= tiles[0].length) return true;
        return tiles[row][col].getType() == 'I';
    }

    /**
     * Return true if (row,col) is the hero tile or one tile away in cardinal directions (up/down/left/right).
     *
     * @param row tile row index to test
     * @param col tile column index to test
     * @return true if within one cardinal tile of hero spawn
     */
    private boolean isWithinOneCardinalTileOfHero(int row, int col) {
        int hr = hero.getTileRow();
        int hc = hero.getTileCol();
        int manhattan = Math.abs(hr - row) + Math.abs(hc - col);
        return manhattan <= 1;
    }

    /**
     * Find nearest valid spawn tile (row,col) scanning outward, avoiding indestructible and the vicinity of hero.
     * Returns [row,col] or null if none found.
     *
     * @param desiredRow desired spawn row
     * @param desiredCol desired spawn column
     * @return an int[2] {row,col} of a valid spawn or null if none exists
     */
    private int[] findNearestValidSpawn(int desiredRow, int desiredCol) {
        // simple brute-force search by increasing Manhattan radius
        int maxRadius = Math.max(tiles.length, tiles[0].length);
        for (int r = 0; r <= maxRadius; r++) {
            for (int dr = -r; dr <= r; dr++) {
                int dc = r - Math.abs(dr);
                // check two symmetrical columns for this dr (dc and -dc)
                int[] rows = {desiredRow + dr, desiredRow + dr};
                int[] cols = {desiredCol + dc, desiredCol - dc};
                for (int k = 0; k < 2; k++) {
                    int rr = rows[k];
                    int cc = cols[k];
                    if (rr < 0 || rr >= tiles.length || cc < 0 || cc >= tiles[0].length) continue;
                    if (isTileIndestructible(rr, cc)) continue;
                    if (isWithinOneCardinalTileOfHero(rr, cc)) continue;
                    // must be walkable or destructible (not barrier)
                    char t = tiles[rr][cc].getType();
                    if (t == ' ' || t == 'D') {
                        return new int[]{rr, cc};
                    }
                }
            }
        }
        return null;
    }

    /**
     * Debug dump to console after startLevel: lists all destructible tiles and drone spawns.
     *
     * @param desiredTotal the intended number of destructible tiles for the level
     */
    private void debugDumpLevelSetup(int desiredTotal) {
        StringBuilder sb = new StringBuilder();
        List<String> destr = new ArrayList<>();
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[r].length; c++) {
                if (tiles[r][c].getType() == 'D') {
                    destr.add("(" + r + "," + c + ")");
                }
            }
        }

        sb.append("Destructible tiles (count=").append(destr.size()).append("): ");
        for (String s : destr) sb.append(s).append(" ");
        System.out.println(sb.toString());

        StringBuilder sd = new StringBuilder();
        sd.append("Drone spawns (col,row): ");
        for (drone d : drones) {
            sd.append("(").append(d.getTileCol()).append(",").append(d.getTileRow()).append(") ");
        }
        System.out.println(sd.toString());
    }

}

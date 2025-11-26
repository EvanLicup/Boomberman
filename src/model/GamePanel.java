package model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * JPanel responsible for rendering the game view.
 * <p>
 * {@code GamePanel} draws the tilemap, hero, drones, bombs, powerups, HUD (score/level),
 * and handles loading of sprite assets. It queries {@link GameModel} for current
 * game state and renders visuals accordingly.
 */
public class GamePanel extends JPanel {

    GameModel gm;
    // SCREEN SETTINGS
    final int maxScreenCol = 17;
    final int maxScreenRow = 11;
    final int screenWidth;
    final int screenHeight;

    Tile[][] tiles;

    public BufferedImage heroUp, heroDown, heroLeft, heroRight, heroIdle, heroDeath,
                        basicTile, slipperyTile, breakableTile, barrierTile, indestructibleTile,
                        borderTopLeft, borderTopRight, borderLeftLine, borderRightLine, borderBottomLeft, borderBottomRight, borderTop, borderBottom,
                        normalBomb, exitImg;
    // player-highlighted base tile
    public BufferedImage playerTile;

    // drone sprites
    public BufferedImage droneUp, droneDown, droneLeft, droneRight;
    // second drone sprite set (fast drone)
    public BufferedImage drone2Up, drone2Down, drone2Left, drone2Right;

    // drone death sprite
    public BufferedImage droneDeath;

    // health icons (battery)
    public BufferedImage health1, health2, health3;

    // walking bomb movement sprites
    public BufferedImage walkingBombUp, walkingBombDown, walkingBombLeft, walkingBombRight;

    // power-up icon
    public BufferedImage powerUpIcon;
    public BufferedImage power1Icon, power2Icon;

    /**
     * Constructs a GamePanel bound to the given {@link GameModel}.
     * <p>
     * The panel sets its preferred size based on the model's tile size and
     * initializes sprite loading (calls {@link #getHeroImage()}, {@link #getBlocksImage()},
     * and {@link #getObjectImage()}).
     *
     * @param gm the game model to render
     */
    public GamePanel(GameModel gm) {
        this.gm = gm;
        this.tiles= gm.tiles;
        this.screenWidth = maxScreenCol * gm.tileSize;
        this.screenHeight = maxScreenRow * gm.tileSize;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(gm.keyH);
        this.setFocusable(true);

        getHeroImage();
        getBlocksImage();
        getObjectImage();

    }

    /**
     * Draws the hero sprite at the hero's current position.
     * <p>
     * The method selects the correct directional sprite and honors the hero's
     * invulnerability blinking by calling {@code gm.hero.isDrawnThisFrame()}.
     *
     * @param g the Graphics context to draw on
     */
    public void drawHero (Graphics g) {
        BufferedImage image = heroIdle; // fallback
        switch(gm.hero.direction) {
            case "up": image = heroUp; break;
            case "down": image = heroDown; break;
            case "left": image = heroLeft; break;
            case "right": image = heroRight; break;
        }
        // blinking while invulnerable
        if (gm.hero.isDrawnThisFrame()) {
            g.drawImage(image, gm.hero.getX(), gm.hero.getY(), gm.tileSize, gm.tileSize, null);
        }
    }

    /**
     * Loads hero and drone sprite images from resources.
     * <p>
     * Silently falls back (leaves images null) if assets are missing so the game remains robust.
     */
    public void getHeroImage () {
        try {
            // HERO SPRITES
            heroUp = ImageIO.read(getClass().getResourceAsStream("/hero/heroUp.png"));
            heroDown = ImageIO.read(getClass().getResourceAsStream("/hero/heroDown.png"));
            heroLeft = ImageIO.read(getClass().getResourceAsStream("/hero/heroLeft.png"));
            heroRight = ImageIO.read(getClass().getResourceAsStream("/hero/heroRight.png"));

            // DRONE SPRITES
            try {
                droneUp = ImageIO.read(getClass().getResourceAsStream("/drone/droneUp.png"));
                droneDown = ImageIO.read(getClass().getResourceAsStream("/drone/droneDown.png"));
                droneLeft = ImageIO.read(getClass().getResourceAsStream("/drone/droneLeft1.png"));
                droneRight = ImageIO.read(getClass().getResourceAsStream("/drone/droneRight1.png"));
                try {
                drone2Up = ImageIO.read(getClass().getResourceAsStream("/drone/drone2Up.png"));
                drone2Down = ImageIO.read(getClass().getResourceAsStream("/drone/drone2Down.png"));
                drone2Left = ImageIO.read(getClass().getResourceAsStream("/drone/drone2Left1.png"));
                drone2Right = ImageIO.read(getClass().getResourceAsStream("/drone/drone2Right1.png"));
                }catch (Exception ex2) {
                // If the second set is missing, leave null so it will fall back to original sprites
                drone2Up = drone2Down = drone2Left = drone2Right = null;}
                // DRONE DEATH
                try {
                    droneDeath = ImageIO.read(getClass().getResourceAsStream("/drone/droneDeath.png"));
                } catch (Exception ex) {
                    droneDeath = null;
                }
                // load walking-bomb movement sprites (used by WalkingBomb)
                try {
                    walkingBombUp    = ImageIO.read(getClass().getResourceAsStream("/powerup/powerup.png"));
                    walkingBombDown  = ImageIO.read(getClass().getResourceAsStream("/powerup/powerdown.png"));
                    walkingBombLeft  = ImageIO.read(getClass().getResourceAsStream("/powerup/powerleft.png"));
                    walkingBombRight = ImageIO.read(getClass().getResourceAsStream("/powerup/powerright.png"));
                } catch (Exception ex) {
                    walkingBombUp = walkingBombDown = walkingBombLeft = walkingBombRight = null;
                }
            } catch (Exception e) {
                // fallback
                droneUp = droneDown = droneLeft = droneRight = null;
                droneDeath = null;
            }


            // ⭐ POWER-UP ITEM SPRITE (the pickup itself)
            try {
                powerUpIcon = ImageIO.read(getClass().getResourceAsStream("/powerup/power.png"));
            } catch (Exception ex) {
                powerUpIcon = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads tiles and border images used to draw the map.
     * <p>
     * Missing assets are tolerated and set to null.
     */
    public void getBlocksImage () {
        try {
            basicTile = ImageIO.read(getClass().getResourceAsStream("/blocks/tile.png"));
            slipperyTile = ImageIO.read(getClass().getResourceAsStream("/blocks/slipperyTile.png"));
            breakableTile = ImageIO.read(getClass().getResourceAsStream("/blocks/breakableTile.png"));
            indestructibleTile = ImageIO.read(getClass().getResourceAsStream("/blocks/indestructibleTile.png"));
            borderTopLeft = ImageIO.read(getClass().getResourceAsStream("/blocks/borderTopLeft.png"));
            borderTopRight = ImageIO.read(getClass().getResourceAsStream("/blocks/borderTopRight.png"));
            borderLeftLine = ImageIO.read(getClass().getResourceAsStream("/blocks/borderLeftLine.png"));
            borderRightLine = ImageIO.read(getClass().getResourceAsStream("/blocks/borderRightLine.png"));
            borderBottomLeft = ImageIO.read(getClass().getResourceAsStream("/blocks/borderBottomLeft.png"));
            borderBottomRight = ImageIO.read(getClass().getResourceAsStream("/blocks/borderBottomRight.png"));
            borderBottom = ImageIO.read(getClass().getResourceAsStream("/blocks/borderBottom.png"));
            borderTop = ImageIO.read(getClass().getResourceAsStream("/blocks/borderTop.png"));

            // exit image
            try {
                exitImg = ImageIO.read(getClass().getResourceAsStream("/blocks/exit.png"));
            } catch (Exception e) {
                exitImg = null;
            }

            // load player-highlight tile (base tile to show hero occupancy)
            try {
                playerTile = ImageIO.read(getClass().getResourceAsStream("/blocks/playertile.png"));
            } catch (Exception e) {
                // if playertile not found, leave null and fallback overlay will be used when drawing
                playerTile = null;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
    /**
     * Loads object images such as bombs and health icons.
     * Individual loads are wrapped to avoid failing the entire load sequence.
     */
    public void getObjectImage () {
        try {
            normalBomb = ImageIO.read(getClass().getResourceAsStream("/objects/normalBomb.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // load health icons; wrapped in individual try/catch so missing files don't break app
        try {
            health3 = ImageIO.read(getClass().getResourceAsStream("/blocks/Health3.png"));
        } catch (Exception e) {
            health3 = null;
        }
        try {
            health2 = ImageIO.read(getClass().getResourceAsStream("/blocks/Health2.png"));
        } catch (Exception e) {
            health2 = null;
        }
        try {
            health1 = ImageIO.read(getClass().getResourceAsStream("/blocks/Health1.png"));
        } catch (Exception e) {
            health1 = null;
        }
        try {
            power1Icon = ImageIO.read(getClass().getResourceAsStream("/powerup/power1.png"));
        } catch (Exception e) {
            power1Icon = null;
        }

        try {
            power2Icon = ImageIO.read(getClass().getResourceAsStream("/powerup/power2.png"));
        } catch (Exception e) {
            power2Icon = null;
        }
    }

    /**
     * Draws all drones from the GameModel onto the Graphics context.
     * <p>
     * The method injects sprites into drone instances if not already set,
     * ensures death sprites are assigned, and falls back to a magenta rectangle
     * when sprites are unavailable.
     *
     * @param g the Graphics context to draw on
     */
    public void drawDrones(Graphics g) {
        if (gm == null) return;
        if (gm.drones == null) return;

        try {
            for (int i = 0; i < gm.drones.size(); i++) {
                drone d = gm.drones.get(i);

                // Choose which sprite set to inject
                if (d.spriteUp == null) {
                    if (d instanceof FastDrone) {
                        // inject fast-drone sprites if available, otherwise fall back to normal set
                        if (drone2Up != null || drone2Down != null || drone2Left != null || drone2Right != null) {
                            d.setSprites(drone2Up != null ? drone2Up : droneUp,
                                        drone2Down != null ? drone2Down : droneDown,
                                        drone2Left != null ? drone2Left : droneLeft,
                                        drone2Right != null ? drone2Right : droneRight);
                        } else if (droneUp != null) {
                            // fallback to original set
                            d.setSprites(droneUp, droneDown, droneLeft, droneRight);
                        }
                    } else {
                        // normal drone: inject default sprite set if present
                        if (droneUp != null) {
                            d.setSprites(droneUp, droneDown, droneLeft, droneRight);
                        }
                    }
                }

                // Ensure death sprite injected (shared)
                if (d.spriteDeath == null && droneDeath != null) {
                    d.setDeathSprite(droneDeath);
                }

                // Ask drone which sprite to draw
                BufferedImage spr = d.getCurrentSprite();

                if (spr != null) {
                    g.drawImage(spr, d.getX(), d.getY(), gm.tileSize, gm.tileSize, null);
                } else {
                    // fallback box
                    g.setColor(Color.MAGENTA);
                    g.fillRect(d.getX(), d.getY(), gm.tileSize, gm.tileSize);
                }
            }
        } catch (Exception e) {
            // keep drawing robust even if a drone errors
            e.printStackTrace();
        }
    }

    /**
     * Draws all tiles, overlays (bomb ranges), powerups, exit and hero highlight.
     * <p>
     * This method computes whether tiles are in bomb ranges (both timed and walking bombs),
     * whether the hero is in danger to change highlight color, and draws tiles accordingly.
     *
     * @param g the Graphics context to draw on
     */
    public void drawTiles(Graphics g) {

        // compute hero tile indices once (guard nulls)
        int heroRow = -1;
        int heroCol = -1;
        if (gm != null && gm.hero != null) {
            heroRow = gm.hero.getTileRow();
            heroCol = gm.hero.getTileCol();
        }

        // decide if hero is in danger from any currently active bomb (timed bombs radius depends on hero powerup, walking bombs radius=2)
        boolean heroInDanger = false;
        if (gm != null && gm.hero != null) {
            // timed bombs: radius depends on hero's radius powerup
            if (gm.bombs != null) {
                for (Bomba b : gm.bombs) {
                    if (!b.exploded) {
                        int br = b.getRow();
                        int bc = b.getCol();
                        int radius = (gm.hero.hasRadiusPowerup() ? 2 : 1);

                        // center
                        if (heroRow == br && heroCol == bc) {
                            if (br >= 0 && br < tiles.length && bc >= 0 && bc < tiles[0].length) {
                                char t = tiles[br][bc].getType();
                                if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                            }
                        }

                        // check cross up to radius
                        for (int d = 1; d <= radius; d++) {
                            // left
                            if (heroRow == br && heroCol == bc - d) {
                                int rr = br, cc = bc - d;
                                if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                    char t = tiles[rr][cc].getType();
                                    if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                                }
                            }
                            // right
                            if (heroRow == br && heroCol == bc + d) {
                                int rr = br, cc = bc + d;
                                if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                    char t = tiles[rr][cc].getType();
                                    if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                                }
                            }
                            // up
                            if (heroCol == bc && heroRow == br - d) {
                                int rr = br - d, cc = bc;
                                if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                    char t = tiles[rr][cc].getType();
                                    if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                                }
                            }
                            // down
                            if (heroCol == bc && heroRow == br + d) {
                                int rr = br + d, cc = bc;
                                if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                    char t = tiles[rr][cc].getType();
                                    if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                                }
                            }
                        } // end for d
                        if (heroInDanger) break;
                    }
                }
            }

            // walking bombs (radius = 2) — unchanged
            if (!heroInDanger && gm.walkingBombs != null) {
                for (WalkingBomb wb : gm.walkingBombs) {
                    if (wb == null || wb.isExploded()) continue;
                    int br = wb.getTileRow();
                    int bc = wb.getTileCol();

                    // center
                    if (heroRow == br && heroCol == bc) {
                        if (br >= 0 && br < tiles.length && bc >= 0 && bc < tiles[0].length) {
                            char t = tiles[br][bc].getType();
                            if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                        }
                    }
                    // distance 1 on cross
                    if (heroRow == br && Math.abs(heroCol - bc) == 1) {
                        int rr = br, cc = heroCol;
                        char t = tiles[rr][cc].getType();
                        if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                    }
                    if (heroCol == bc && Math.abs(heroRow - br) == 1) {
                        int rr = heroRow, cc = bc;
                        char t = tiles[rr][cc].getType();
                        if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                    }
                    // distance 2 on cross
                    if (heroRow == br && Math.abs(heroCol - bc) == 2) {
                        int rr = br, cc = heroCol;
                        if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                            char t = tiles[rr][cc].getType();
                            if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                        }
                    }
                    if (heroCol == bc && Math.abs(heroRow - br) == 2) {
                        int rr = heroRow, cc = bc;
                        if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                            char t = tiles[rr][cc].getType();
                            if (t == ' ' || t == 'D') { heroInDanger = true; break; }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                char c = tiles[i][j].getType();

                // determine if this tile is hero's tile
                boolean isHeroTile = (i == heroRow && j == heroCol);

                // determine if this tile is within any bomb's range (explosion radius overlay)
                // but only if the tile being considered can actually be affected (walkable or destructible)
                boolean tileInBombRange = false;

                // normal timed bombs (variable radius: 1 or 2 depending on hero powerup)
                if (gm != null && gm.bombs != null) {
                    for (Bomba b : gm.bombs) {
                        if (!b.exploded) {
                            int br = b.getRow();
                            int bc = b.getCol();
                            int radius = (gm.hero != null && gm.hero.hasRadiusPowerup()) ? 2 : 1;

                            // center
                            if (i == br && j == bc) {
                                if (br >= 0 && br < tiles.length && bc >= 0 && bc < tiles[0].length) {
                                    char t = tiles[br][bc].getType();
                                    if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                                }
                            }

                            // check cross up to radius
                            for (int d = 1; d <= radius; d++) {
                                // down
                                if (i == br + d && j == bc) {
                                    int rr = br + d, cc = bc;
                                    if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                        char t = tiles[rr][cc].getType();
                                        if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                                    }
                                }
                                // up
                                if (i == br - d && j == bc) {
                                    int rr = br - d, cc = bc;
                                    if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                        char t = tiles[rr][cc].getType();
                                        if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                                    }
                                }
                                // right
                                if (i == br && j == bc + d) {
                                    int rr = br, cc = bc + d;
                                    if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                        char t = tiles[rr][cc].getType();
                                        if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                                    }
                                }
                                // left
                                if (i == br && j == bc - d) {
                                    int rr = br, cc = bc - d;
                                    if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                        char t = tiles[rr][cc].getType();
                                        if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                                    }
                                }
                            } // end for d

                            if (tileInBombRange) break;
                        }
                    }
                }

                // walking bombs (expanded radius = 2) - unchanged
                if (!tileInBombRange && gm != null && gm.walkingBombs != null) {
                    for (WalkingBomb wb : gm.walkingBombs) {
                        if (wb == null || wb.isExploded()) continue;
                        int br = wb.getTileRow();
                        int bc = wb.getTileCol();

                        // center and cross up to distance 2
                        if (i == br && j == bc) {
                            char t = tiles[br][bc].getType();
                            if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                        }
                        if (i == br + 1 && j == bc) {
                            int rr = br + 1, cc = bc;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br - 1 && j == bc) {
                            int rr = br - 1, cc = bc;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br && j == bc + 1) {
                            int rr = br, cc = bc + 1;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br && j == bc - 1) {
                            int rr = br, cc = bc - 1;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        // distance 2
                        if (i == br + 2 && j == bc) {
                            int rr = br + 2, cc = bc;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br - 2 && j == bc) {
                            int rr = br - 2, cc = bc;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br && j == bc + 2) {
                            int rr = br, cc = bc + 2;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                        if (i == br && j == bc - 2) {
                            int rr = br, cc = bc - 2;
                            if (rr >= 0 && rr < tiles.length && cc >= 0 && cc < tiles[0].length) {
                                char t = tiles[rr][cc].getType();
                                if (t == ' ' || t == 'D') { tileInBombRange = true; break; }
                            }
                        }
                    }
                }

                // When drawing the base "basicTile", if hero stands on it we draw playerTile instead.
                BufferedImage baseForBasic = isHeroTile && playerTile != null ? playerTile : basicTile;

                if (c == 'I') {
                    g.drawImage(baseForBasic, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                    g.drawImage(indestructibleTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'D') {
                    g.drawImage(baseForBasic, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                    if (tiles[i][j].getDestroyedStatus() == false)
                        g.drawImage(breakableTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == ' ') {
                    g.drawImage(baseForBasic, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'B') {
                    g.drawImage(borderBottom, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'L') {
                    g.drawImage(borderLeftLine, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'R') {
                    g.drawImage(borderRightLine, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'T') {
                    g.drawImage(borderTop, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == '1') {
                    g.drawImage(borderTopLeft, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == '2') {
                    g.drawImage(borderTopRight, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == '3') {
                    g.drawImage(borderBottomLeft, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == '4') {
                    g.drawImage(borderBottomRight, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                }

                // draw exit image if placed here
                if (gm.exitRow == i && gm.exitCol == j && exitImg != null) {
                    g.drawImage(exitImg, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                }
                // draw powerup icon if present here (visual pickup)
                if (gm.powerups != null) {
                    for (PowerUp pu : gm.powerups) {
                        if (pu != null && !pu.isPicked() && pu.row == i && pu.col == j) {
                            BufferedImage icon = null;

                            switch (pu.type) {
                                case WALKING:
                                    icon = powerUpIcon;      // your old power.png
                                    break;
                                case RADIUS:
                                    icon = power1Icon;       // new power1.png
                                    break;
                                case EXTRA_LIFE:
                                    icon = power2Icon;       // new power2.png
                                    break;
                            }

                            if (icon != null) {
                                g.drawImage(icon, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                            }
                            break;
                        }
                    }
                }

                // Explosion radius overlay (translucent red) for tiles in any bomb's range
                if (tileInBombRange) {
                    Graphics2D g2 = (Graphics2D) g;
                    Composite old = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                    g2.setColor(Color.RED);
                    g2.fillRect(j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize);
                    g2.setComposite(old);
                }

                // If this is the hero tile and playerTile image is null, draw a translucent overlay (cyan normally)
                if (isHeroTile && playerTile == null) {
                    Graphics2D g2 = (Graphics2D) g;
                    Composite old = g2.getComposite();

                    if (heroInDanger) {
                        // pulsing alpha for danger blink (0.25 - 0.7)
                        double t = System.currentTimeMillis() / 200.0;
                        float pulse = (float) ((Math.sin(t) + 1.0) / 2.0); // 0..1
                        float alpha = 0.35f + 0.35f * pulse; // 0.35..0.7
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                        g2.setColor(Color.RED);
                        g2.fillRect(j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize);
                    } else {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                        g2.setColor(Color.CYAN);
                        g2.fillRect(j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize);
                    }

                    g2.setComposite(old);
                }

                // If playerTile exists and this is hero tile and hero is in danger, overlay a pulsing red on top
                if (isHeroTile && playerTile != null && heroInDanger) {
                    Graphics2D g2 = (Graphics2D) g;
                    Composite old = g2.getComposite();
                    double t = System.currentTimeMillis() / 200.0;
                    float pulse = (float) ((Math.sin(t) + 1.0) / 2.0); // 0..1
                    float alpha = 0.25f + 0.45f * pulse; // e.g. 0.25..0.7
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2.setColor(Color.RED);
                    g2.fillRect(j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize);
                    g2.setComposite(old);
                }
            }
        }
    }




    /**
     * Draws a bomb (normal or powered) at its tile location.
     *
     * @param g2 the Graphics2D context
     * @param b the bomb to draw
     */
    public void drawBomb(Graphics2D g2, Bomba b) {
        // If the bomb has the powered flag and powerUpIcon exists, draw that image
        if (b.isPowered()) {
            g2.drawImage(powerUpIcon, b.getCol() * gm.tileSize, b.getRow() * gm.tileSize, gm.tileSize, gm.tileSize, null);
        } else {
            g2.drawImage(normalBomb, b.getCol() * gm.tileSize, b.getRow() * gm.tileSize, gm.tileSize, gm.tileSize, null);
        }
    }

    /**
     * Paints the entire game scene. This method orchestrates calls to drawTiles,
     * drawDrones, drawWalkingBombs, drawHero, and renders HUD elements.
     *
     * @param g the Graphics context provided by Swing
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);

        // draw drones (below hero, change order if you want drones over hero)
        // draw drones (below hero)
        drawDrones(g2d);

        // draw walking bombs (they are separate entities; draw below hero or above as you like)
        drawWalkingBombs(g2d);

        // draw hero
        drawHero(g2d);

        // draw remote exit tile text / info overlay
        // display score & level
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        g2d.drawString("Level: " + gm.level, 8, 16);
        g2d.drawString("Score: " + gm.score, 8, 34);
        g2d.drawString("Crates: " + gm.destroyedCratesCount + "/" + gm.requiredCratesToUnlockExit, 8, 52);

        if (gm.message != null) {
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
            FontMetrics fm = g2d.getFontMetrics();
            int mw = fm.stringWidth(gm.message);
            int mx = (screenWidth - mw) / 2;
            int my = 28;
            // translucent bg
            Composite oldc = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(mx - 8, my - fm.getAscent(), mw + 16, fm.getAscent() + 8, 8, 8);
            g2d.setComposite(oldc);

            g2d.setColor(Color.WHITE);
            g2d.drawString(gm.message, mx, my);
        }
        // Draw health icon top-right
        int iconSize = gm.tileSize; // 48
        int padding = 8;
        int hx = screenWidth - iconSize - padding;
        int hy = 8;

        BufferedImage healthImg = null;
        int hearts = (gm.hero != null) ? gm.hero.getHearts() : 0;
        if (hearts >= 3) healthImg = health3;
        else if (hearts == 2) healthImg = health2;
        else if (hearts == 1) healthImg = health1;
        // If health images missing, draw simple text fallback
        if (healthImg != null) {
            g2d.drawImage(healthImg, hx, hy, iconSize, iconSize, null);
        } else {
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
            g2d.drawString("Lives: " + hearts, hx - 8, hy + 20);
        }

        for (int i = 0; i < gm.bombs.size(); i++) {
            Bomba b = gm.bombs.get(i);
            if (b.exploded == false) {
                drawBomb(g2d, b);
            }
        }
        // draw walking bombs (visible, movement sprites)
        drawWalkingBombs(g2d);
        // check if hero is standing on exit and advance
        gm.onHeroReachExit();

        // If game over, overlay message
          if (gm.gameOver || gm.finishedAllLevels) {
            Graphics2D g2 = (Graphics2D) g2d;
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setComposite(old);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));

            String title;
            if (gm.gameOver) {
                title = "GAME OVER";
            } else {
                title = "CONGRATULATIONS";
            }

            FontMetrics fm = g2.getFontMetrics();
            int tx = (screenWidth - fm.stringWidth(title)) / 2;
            int ty = (screenHeight / 2);
            g2.drawString(title, tx, ty);

            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
            String sub;
            if (gm.gameOver) {
                sub = "Final score: " + gm.score;
            } else {
                sub = "You completed all levels! Final score: " + gm.score;
            }
            int sx = (screenWidth - g2.getFontMetrics().stringWidth(sub)) / 2;
            g2.drawString(sub, sx, ty + 36);
        }

        g2d.dispose();

    }
    /**
     * Draws all walking bombs (movable bombs) and injects their movement sprites if needed.
     *
     * @param g the Graphics context to draw on
     */
    public void drawWalkingBombs(Graphics g) {
        if (gm == null || gm.walkingBombs == null) return;
        for (WalkingBomb wb : gm.walkingBombs) {
            if (wb == null) continue;
            // inject sprites from GamePanel if not set already
            if (wb.spriteUp == null && walkingBombUp != null) {
                wb.setSprites(walkingBombUp, walkingBombDown, walkingBombLeft, walkingBombRight);
            }
            BufferedImage spr = wb.getCurrentSprite();
            if (spr != null) {
                g.drawImage(spr, wb.getX(), wb.getY(), gm.tileSize, gm.tileSize, null);
            } else {
                // default: draw normal bomb if movement sprite missing
                g.drawImage(normalBomb, wb.getTileCol() * gm.tileSize, wb.getTileRow() * gm.tileSize, gm.tileSize, gm.tileSize, null);
            }
        }
    }
}
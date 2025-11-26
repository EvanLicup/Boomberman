package model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
                        normalBomb, playerTile, droneUp, droneDown, droneLeft, droneRight;


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

        getEntitiesImage();
        getBlocksImage();
        getObjectImage();

    }



    public void drawHero (Graphics g) {
        BufferedImage image = null;

        switch(gm.hero.direction) {
            case "up":
                image = heroUp;
                break;
            case "down":
                image = heroDown;
                break;
            case "left":
                image = heroLeft;
                break;
            case "right":
                image = heroRight;
                break;
        }
        g.drawImage(image, gm.hero.getX(), gm.hero.getY(), gm.tileSize, gm.tileSize, null);
    }

    public void getEntitiesImage () {
        try {
            heroUp = ImageIO.read(getClass().getResourceAsStream("/hero/heroUp.png"));
            heroDown = ImageIO.read(getClass().getResourceAsStream("/hero/heroDown.png"));
            heroLeft = ImageIO.read(getClass().getResourceAsStream("/hero/heroLeft.png"));
            heroRight = ImageIO.read(getClass().getResourceAsStream("/hero/heroRight.png"));

            // load drone sprites (in /res/drone/ or adjust path)
            try {
                droneUp = ImageIO.read(getClass().getResourceAsStream("/drone/droneup.png"));
                droneDown = ImageIO.read(getClass().getResourceAsStream("/drone/dronedown.png"));
                droneLeft = ImageIO.read(getClass().getResourceAsStream("/drone/droneleft.png"));
                droneRight = ImageIO.read(getClass().getResourceAsStream("/drone/droneright.png"));
            } catch (Exception e) {
                // if drone images not found, set null; drawDrones will fallback to rectangle
                droneUp = droneDown = droneLeft = droneRight = null;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public void getObjectImage () {
        try {
            normalBomb = ImageIO.read(getClass().getResourceAsStream("/objects/normalBomb.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Draw all drones (if gm.drones is present)
    public void drawDrones(Graphics g) {
        if (gm == null) return;
        try {
            if (gm.drones == null) return;
            for (int i = 0; i < gm.drones.size(); i++) {
                drone d = gm.drones.get(i);
                // ensure sprites are set on drone (non-destructive)
                if (d.spriteUp == null && droneUp != null) {
                    d.setSprites(droneUp, droneDown, droneLeft, droneRight);
                }
                BufferedImage spr = d.getCurrentSprite();
                if (spr != null) {
                    g.drawImage(spr, d.getX(), d.getY(), gm.tileSize, gm.tileSize, null);
                } else {
                    // fallback: draw a magenta rectangle so you can see the drone
                    g.setColor(Color.MAGENTA);
                    g.fillRect(d.getX(), d.getY(), gm.tileSize, gm.tileSize);
                }
            }
        } catch (Exception e) {
            // keep drawing robust even if drones list isn't present or a drone throws
        }
    }

    public void drawTiles(Graphics g) {

        // compute hero tile indices once (guard nulls)
        int heroRow = -1;
        int heroCol = -1;
        if (gm != null && gm.hero != null) {
            heroRow = gm.hero.getTileRow();
            heroCol = gm.hero.getTileCol();
        }

        // decide if hero is in danger from any currently active bomb (range = 1, cross pattern)
        boolean heroInDanger = false;
        if (gm != null && gm.bombs != null && gm.hero != null) {
            for (Bomba b : gm.bombs) {
                if (!b.exploded) {
                    int br = b.getRow();
                    int bc = b.getCol();
                    if (heroRow >= br - 1 && heroRow <= br + 1 && heroCol >= bc - 1 && heroCol <= bc + 1) {
                        heroInDanger = true;
                        break;
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
                boolean tileInBombRange = false;
                if (gm != null && gm.bombs != null) {
                    for (Bomba b : gm.bombs) {
                        if (!b.exploded) {
                            int br = b.getRow();
                            int bc = b.getCol();
                            // cross pattern: center + up/down/left/right
                            if ((i == br && j == bc) ||
                                (i == br + 1 && j == bc) ||
                                (i == br - 1 && j == bc) ||
                                (i == br && j == bc + 1) ||
                                (i == br && j == bc - 1)) {
                                tileInBombRange = true;
                                break;
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

    public void drawBomb(Graphics2D g2, Bomba b) {
        g2.drawImage(normalBomb, b.getCol() * gm.tileSize, b.getRow() * gm.tileSize, gm.tileSize, gm.tileSize, null);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);

        // draw drones (below hero, change order if you want drones over hero)
        drawDrones(g2d);

        drawHero(g2d);

        for (int i = 0; i < gm.bombs.size(); i++) {
            Bomba b = gm.bombs.get(i);
            if (b.exploded == false) {
                drawBomb(g2d, b);
            }
        }

        g2d.dispose();

    }

    /**
     * Destroys a destructible tile at the specified coordinates.
     * The tile is replaced with a main.WalkableTile, allowing movement through it.
     *
     * @param row the row index of the tile to destroy
     * @param col the column index of the tile to destroy
     * Precondition: The coordinates must be within the bounds of the board
     */
    /*
    public void destroyTile(int row, int col) {

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            if (tileBoard[row][col].getType() == 'D') {
                tileBoard[row][col] = new WalkableTile(row, col);
            }
        }
    }

     */
}

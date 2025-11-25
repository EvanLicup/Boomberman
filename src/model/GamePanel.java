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
                        normalBomb;



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



    public void drawHero (Graphics g) {
        /*
        g.setColor(Color.white);
        g.fillRect(hero.getX(), hero.getY(), tileSize, tileSize);

         */

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

    public void getHeroImage () {
        try {
            heroUp = ImageIO.read(getClass().getResourceAsStream("/hero/heroUp.png"));
            heroDown = ImageIO.read(getClass().getResourceAsStream("/hero/heroDown.png"));
            heroLeft = ImageIO.read(getClass().getResourceAsStream("/hero/heroLeft.png"));
            heroRight = ImageIO.read(getClass().getResourceAsStream("/hero/heroRight.png"));

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

    public void drawTiles(Graphics g) {

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                char c = tiles[i][j].getType();

                if (c == 'I') {
                    g.drawImage(basicTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                    g.drawImage(indestructibleTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == 'D') {
                    g.drawImage(basicTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                    g.drawImage(breakableTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (c == ' ') {
                    g.drawImage(basicTile, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == 'B') {
                    g.drawImage(borderBottom, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == 'L') {
                    g.drawImage(borderLeftLine, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == 'R') {
                    g.drawImage(borderRightLine, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == 'T') {
                    g.drawImage(borderTop, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == '1') {
                    g.drawImage(borderTopLeft, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == '2') {
                    g.drawImage(borderTopRight, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == '3') {
                    g.drawImage(borderBottomLeft, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                } else if (gm.inputMap[i][j] == '4') {
                    g.drawImage(borderBottomRight, j * gm.tileSize, i * gm.tileSize, gm.tileSize, gm.tileSize, null);
                }

            }
        }
    }

    public void drawBomb(Graphics2D g2, Bomba b) {


        g2.drawImage(normalBomb, b.getX() * gm.tileSize, b.getY() * gm.tileSize, gm.tileSize, gm.tileSize, null);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);
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

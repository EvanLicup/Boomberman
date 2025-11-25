package main;

import object.Bomba;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 32;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    final int maxScreenCol = 17;
    final int maxScreenRow = 11;
    final int screenWidth = maxScreenCol * tileSize;
    final int screenHeight = maxScreenRow * tileSize;
    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    ArrayList<Bomba>bombs = new ArrayList<>();
    Hero hero = new Hero(10,10,3, this, keyHandler);



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


    Tile tiles[][];




    public BufferedImage heroUp, heroDown, heroLeft, heroRight, heroIdle, heroDeath,
                        basicTile, slipperyTile, breakableTile, barrierTile, indestructibleTile,
                        borderTopLeft, borderTopRight, borderLeftLine, borderRightLine, borderBottomLeft, borderBottomRight, borderTop, borderBottom,
                        normalBomb;



    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.tiles = new Tile[inputMap.length][inputMap[0].length];
        getHeroImage();
        getBlocksImage();
        getObjectImage();
        initializeTiles();

    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run () {

        double drawInterval = 1000000000 / 60.0;
        double delta = 0;
        double deltaBomb = 0;
        long lastTime = System.nanoTime();
        long currentTime;


        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            deltaBomb += (currentTime - lastTime) / 1000000000.0;
            lastTime = currentTime;

            if (delta >= 1) {
                update(deltaBomb);
                repaint();
                delta--;
                deltaBomb = 0;
            }


        }
    }

    public void update(double delta) {
        hero.update();

        for (int i = 0; i < bombs.size(); i++) {
            Bomba b = bombs.get(i);
            b.decreaseTime(delta);
        }

        bombs.removeIf(b -> b.exploded);

    }

    public void drawHero (Graphics g) {
        /*
        g.setColor(Color.white);
        g.fillRect(hero.getX(), hero.getY(), tileSize, tileSize);

         */

        BufferedImage image = null;

        switch(hero.direction) {
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
        g.drawImage(image, hero.getX(), hero.getY(), tileSize, tileSize, null);


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
            normalBomb = ImageIO.read(getClass().getResourceAsStream("/objects/bomb.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                }
                else if (c == 'L') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == 'R') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == 'T') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == '1') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == '2') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == '3') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else if (c == '4') {
                    tiles[i][j] = new BarrierTile(i, j);
                }
                else {
                    System.out.println("Unknown char at (" + i + "," + j + ")");
                    tiles[i][j] = null;
                }
            }
        }
    }


    public void drawTiles(Graphics g) {

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                char c = tiles[i][j].getType();

                if (c == 'I') {
                    g.drawImage(basicTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
                    g.drawImage(indestructibleTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (c == 'D') {
                    g.drawImage(basicTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
                    g.drawImage(breakableTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (c == ' ') {
                    g.drawImage(basicTile, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == 'B') {
                    g.drawImage(borderBottom, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == 'L') {
                    g.drawImage(borderLeftLine, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == 'R') {
                    g.drawImage(borderRightLine, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == 'T') {
                    g.drawImage(borderTop, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == '1') {
                    g.drawImage(borderTopLeft, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == '2') {
                    g.drawImage(borderTopRight, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == '3') {
                    g.drawImage(borderBottomLeft, j * tileSize, i * tileSize, tileSize, tileSize, null);
                } else if (inputMap[i][j] == '4') {
                    g.drawImage(borderBottomRight, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }
            }
        }
    }

    public void drawBomb(Graphics2D g2, Bomba b) {


        g2.drawImage(normalBomb, b.x * tileSize, b.y * tileSize, tileSize, tileSize, null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);
        drawHero(g2d);

        System.out.println("hero x" + hero.getX() + " y" + hero.getY());

        for (int i = 0; i < bombs.size(); i++) {
            Bomba b = bombs.get(i);
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

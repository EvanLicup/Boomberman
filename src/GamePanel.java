import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 32;
    final int scale = 2;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    final int maxScreenCol = 20;
    final int maxScreenRow = 20;
    final int screenWidth = maxScreenCol * tileSize;
    final int screenHeight = maxScreenRow * tileSize;
    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();
    Hero hero = new Hero(10,10,3, this, keyHandler);


    char[][] inputMap = {
            {'B','T','T','T','T','T','T','T','T','T','T','T', 'B'},
            {'L','D',' ','D',' ',' ','D',' ',' ','D','D',' ', 'R'},
            {'L','D','I',' ','I','D','I','D','I',' ','I',' ', 'R'},
            {'L',' ',' ','D',' ',' ','D',' ','D',' ','D','D', 'R'},
            {'L',' ','I',' ','I','D','I',' ','I',' ','I','D', 'R'},
            {'L','D',' ','D','D',' ','D','D',' ','D',' ',' ', 'R'},
            {'L',' ','I','D','I',' ','I',' ','I','D','I','D', 'R'},
            {'L','D',' ',' ','D',' ',' ',' ','D','D','D',' ', 'R'},
            {'B','B','B','B','B','B','B','B','B','B','B','B', 'B'}
    };


    Tile tiles[][];




    public BufferedImage heroUp, heroDown, heroLeft, heroRight, heroIdle, heroDeath,
                        basicTile, slipperyTile, breakableTile, barrierTile, indestructibleTile,
                        borderTopLeft, borderTopRight, borderLeftLine, borderRightLine, borderBottomLeft, borderBottomRight;



    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        this.tiles = new Tile[inputMap.length][inputMap[0].length];
        getHeroImage();
        getBlocksImage();

    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run () {

        double drawInterval = 1000000000 /60.0;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;


        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }


        }
    }

    public void update() {
        hero.update();


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
            heroUp = ImageIO.read(getClass().getResourceAsStream("/hero/Sprite_Heart.png"));
            heroDown = ImageIO.read(getClass().getResourceAsStream("/hero/Sprite_Heart.png"));
            heroLeft = ImageIO.read(getClass().getResourceAsStream("/hero/Sprite_Heart.png"));
            heroRight = ImageIO.read(getClass().getResourceAsStream("/hero/Sprite_Heart.png"));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBlocksImage () {
        try {
            basicTile = ImageIO.read(getClass().getResourceAsStream("/blocks/tile.png"));
            slipperyTile = ImageIO.read(getClass().getResourceAsStream("/blocks/slipperyTile_32.png"));
            breakableTile = ImageIO.read(getClass().getResourceAsStream("/blocks/breakableTile.png"));
            indestructibleTile = ImageIO.read(getClass().getResourceAsStream("/blocks/barrierTile_32.png"));
            borderTopLeft = ImageIO.read(getClass().getResourceAsStream("/blocks/borderLeft.png"));
            borderTopRight = ImageIO.read(getClass().getResourceAsStream("/blocks/borderRight.png"));
            borderLeftLine = ImageIO.read(getClass().getResourceAsStream("/blocks/borderLeftLine.png"));
            borderRightLine = ImageIO.read(getClass().getResourceAsStream("/blocks/borderRightLine.png"));
            borderBottomLeft = ImageIO.read(getClass().getResourceAsStream("/blocks/borderBottomLeft.png"));
            borderBottomRight = ImageIO.read(getClass().getResourceAsStream("/blocks/borderBottomRight.png"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }
    

    public void drawTiles (Graphics g) {


        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (inputMap[i][j] == 'I') {
                    g.drawImage(indestructibleTile, i * tileSize, j * tileSize, tileSize, tileSize, null);
                }
                else if (inputMap[i][j] == 'D') {
                    g.drawImage(basicTile, i * tileSize, j * tileSize, tileSize, tileSize, null);
                    g.drawImage(breakableTile, i * tileSize, j * tileSize, tileSize, tileSize, null);
                }
                else if (inputMap[i][j] == ' ') {
                    g.drawImage(basicTile, i * tileSize, j * tileSize, tileSize, tileSize, null);
                }
                else if (inputMap[i][j] == 'B') {

                }
            }
        }
        g.drawImage(borderTopLeft, 0, 0, tileSize, tileSize, null);

        g.drawImage(borderLeftLine, 0 * tileSize, 1 * tileSize, tileSize, tileSize, null);





    }





    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);
        drawHero(g2d);



        g2d.dispose();

    }
}

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48x48 tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 16;
    final int screenWidth = maxScreenCol * tileSize;
    final int screenHeight = maxScreenRow * tileSize;
    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run () {
        while (gameThread != null) {
            double drawInterval = 1000000000/ 60;
            double nextDrawTime = System.nanoTime() + drawInterval;

            update();
            repaint();
        }
    }

    public void update(Hero hero) {
        if (keyHandler.upPressed) {
            hero.getY() -= hero.getHeroSpeed();
        }
        else if (keyHandler.downPressed) {

        }
        else if (keyHandler.leftPressed) {

        }
        else if (keyHandler.rightPressed) {

        }


    }
}

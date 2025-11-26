package gamethread;

import model.GameModel;
import model.GamePanel;

public class Gamethread implements Runnable {
    Thread gameThread = new Thread(this);
    private GameModel gm;
    private GamePanel gp;

    public Gamethread(GameModel gm, GamePanel gp) {
        this.gm = gm;
        this.gp = gp;
    }
    public void startGameThread() {
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
                gm.update(deltaBomb);
                gp.repaint();
                delta--;
                deltaBomb = 0;
            }


        }
    }
}

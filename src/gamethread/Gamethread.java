package gamethread;

import model.GameModel;
import model.GamePanel;

/**
 * The {@code Gamethread} class represents the main game loop responsible for
 * updating the game state and refreshing the game's visual display.
 *
 * <p>This thread continuously:
 * <ul>
 *     <li>Calculates elapsed time between frames</li>
 *     <li>Updates the {@link GameModel} based on time passed</li>
 *     <li>Triggers a screen repaint through {@link GamePanel}</li>
 * </ul>
 *
 * <p>The loop targets a refresh rate of approximately 60 FPS.</p>
 *
 * <p><strong>Important:</strong>
 * The game loop only terminates when {@code gameThread} becomes {@code null},
 * meaning external components should set {@code gameThread = null} to stop the game.</p>
 */
public class Gamethread implements Runnable {

    /** Internal thread running the game loop. */
    Thread gameThread = new Thread(this);

    /** Reference to the game's model, containing state and logic. */
    private GameModel gm;

    /** Reference to the game's rendering panel. */
    private GamePanel gp;

    /**
     * Creates a new game loop thread bound to the given model and panel.
     *
     * @param gm the game model to be updated each frame
     * @param gp the panel to repaint each frame
     */
    public Gamethread(GameModel gm, GamePanel gp) {
        this.gm = gm;
        this.gp = gp;
    }

    /**
     * Starts the internal game loop thread.
     */
    public void startGameThread() {
        gameThread.start();
    }

    /**
     * The main game loop.
     *
     * <p>This loop maintains a fixed update rate of ~60 updates per second.
     * Game logic and physics are updated using {@code deltaBomb}, which
     * represents the actual elapsed time between updates in seconds.</p>
     */
    @Override
    public void run() {

        // Target time between frames (1 second / 60)
        double drawInterval = 1000000000 / 60.0;

        double delta = 0;
        double deltaBomb = 0;

        long lastTime = System.nanoTime();
        long currentTime;

        // Run until the gameThread reference is cleared externally
        while (gameThread != null) {

            currentTime = System.nanoTime();

            // Time passed since last frame
            delta += (currentTime - lastTime) / drawInterval;
            deltaBomb += (currentTime - lastTime) / 1000000000.0; // in seconds

            lastTime = currentTime;

            // Process update + render when a full frame interval has passed
            if (delta >= 1) {

                // Update game logic (movement, drones, bombs, collisions)
                gm.update(deltaBomb);

                // Redraw visual components
                gp.repaint();

                // Reset counters for next frame
                delta--;
                deltaBomb = 0;
            }
        }
    }
}

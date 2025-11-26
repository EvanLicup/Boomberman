package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles all keyboard input for the game, including:
 * <ul>
 *     <li>Hero movement using <b>WASD</b></li>
 *     <li>Walking Bomb movement using <b>arrow keys</b></li>
 *     <li>Bomb actions:
 *         <ul>
 *             <li><b>H</b> — place bomb / spawn walking bomb</li>
 *             <li><b>J</b> — detonate walking bomb</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p>This class tracks which keys are currently pressed using boolean flags
 * that the GameModel and other game systems read every update cycle.</p>
 *
 * <p>Key press behavior:
 * <ul>
 *     <li>Movement keys behave continuously (true while held down).</li>
 *     <li>Bomb placement triggers only once per key press.</li>
 *     <li>Detonation triggers once and resets on key release.</li>
 * </ul>
 */
public class KeyHandler implements KeyListener {

    // -------------------------------------------------------------
    // Movement flags for the HERO (WASD)
    // -------------------------------------------------------------

    /** True while W is held — moves hero upward. */
    public boolean upPressed;

    /** True while S is held — moves hero downward. */
    public boolean downPressed;

    /** True while A is held — moves hero left. */
    public boolean leftPressed;

    /** True while D is held — moves hero right. */
    public boolean rightPressed;


    // -------------------------------------------------------------
    // Movement flags for the WALKING BOMB (arrow keys)
    // -------------------------------------------------------------

    /** True while the ↑ arrow is held — moves walking bomb upward. */
    public boolean bombUpPressed;

    /** True while the ↓ arrow is held — moves walking bomb downward. */
    public boolean bombDownPressed;

    /** True while the ← arrow is held — moves walking bomb left. */
    public boolean bombLeftPressed;

    /** True while the → arrow is held — moves walking bomb right. */
    public boolean bombRightPressed;


    // -------------------------------------------------------------
    // Bomb action keys (H to place, J to detonate)
    // -------------------------------------------------------------

    /**
     * True for one frame when H is pressed. Used to place a bomb or
     * spawn a walking bomb if that powerup is active.
     */
    public boolean placePressed;

    /**
     * Internal flag used to prevent repeated bomb placements while H
     * is held down. Ensures the action triggers only once per key press.
     */
    public boolean placeKeyDown;

    /** True while J is held — triggers detonation of walking bomb. */
    public boolean detonatePressed;


    @Override
    public void keyTyped(KeyEvent e) { }


    /**
     * Handles the moments when keys are pressed down.
     *
     * @param e key event containing the pressed key code
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // ---------------- HERO MOVEMENT (WASD) ----------------
        if (key == KeyEvent.VK_W) {
            upPressed = true;
        }
        else if (key == KeyEvent.VK_S) {
            downPressed = true;
        }
        else if (key == KeyEvent.VK_A) {
            leftPressed = true;
        }
        else if (key == KeyEvent.VK_D) {
            rightPressed = true;
        }

        // ------------- WALKING BOMB MOVEMENT (ARROWS) ----------
        else if (key == KeyEvent.VK_UP) {
            bombUpPressed = true;
        }
        else if (key == KeyEvent.VK_DOWN) {
            bombDownPressed = true;
        }
        else if (key == KeyEvent.VK_LEFT) {
            bombLeftPressed = true;
        }
        else if (key == KeyEvent.VK_RIGHT) {
            bombRightPressed = true;
        }

        // ---------------- BOMB ACTIONS ----------------
        else if (key == KeyEvent.VK_H) {
            // Only trigger once per press
            if (!placeKeyDown) {
                placePressed = true;
                placeKeyDown = true;
            }
        }
        else if (key == KeyEvent.VK_J) {
            detonatePressed = true;
        }
    }


    /**
     * Handles the moments when keys are released.
     *
     * @param e key event containing the released key code
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // ---------------- HERO MOVEMENT ----------------
        if (key == KeyEvent.VK_W) {
            upPressed = false;
        }
        else if (key == KeyEvent.VK_S) {
            downPressed = false;
        }
        else if (key == KeyEvent.VK_A) {
            leftPressed = false;
        }
        else if (key == KeyEvent.VK_D) {
            rightPressed = false;
        }

        // ------------- WALKING BOMB MOVEMENT --------------
        else if (key == KeyEvent.VK_UP) {
            bombUpPressed = false;
        }
        else if (key == KeyEvent.VK_DOWN) {
            bombDownPressed = false;
        }
        else if (key == KeyEvent.VK_LEFT) {
            bombLeftPressed = false;
        }
        else if (key == KeyEvent.VK_RIGHT) {
            bombRightPressed = false;
        }

        // ---------------- BOMB ACTIONS ----------------
        else if (key == KeyEvent.VK_J) {
            detonatePressed = false;
        }
        else if (key == KeyEvent.VK_H) {
            placeKeyDown = false;
        }
    }
}

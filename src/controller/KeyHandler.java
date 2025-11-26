package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // WASD for hero
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // arrow keys for walking-bomb movement
    public boolean bombUpPressed, bombDownPressed, bombLeftPressed, bombRightPressed;

    // bomb actions (H places, J detonates)
    public boolean placePressed, detonatePressed, placeKeyDown;

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // hero (WASD)
        if (key == KeyEvent.VK_W) upPressed = true;
        else if (key == KeyEvent.VK_S) downPressed = true;
        else if (key == KeyEvent.VK_A) leftPressed = true;
        else if (key == KeyEvent.VK_D) rightPressed = true;

        // walking-bomb (arrow keys)
        else if (key == KeyEvent.VK_UP) bombUpPressed = true;
        else if (key == KeyEvent.VK_DOWN) bombDownPressed = true;
        else if (key == KeyEvent.VK_LEFT) bombLeftPressed = true;
        else if (key == KeyEvent.VK_RIGHT) bombRightPressed = true;

        // place/detonate
        else if (key == KeyEvent.VK_H) {
            if (!placeKeyDown) {
                placePressed = true;
                placeKeyDown = true;
            }
        }
        else if (key == KeyEvent.VK_J) {
            detonatePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // hero
        if (key == KeyEvent.VK_W) upPressed = false;
        else if (key == KeyEvent.VK_S) downPressed = false;
        else if (key == KeyEvent.VK_A) leftPressed = false;
        else if (key == KeyEvent.VK_D) rightPressed = false;

        // walking-bomb
        else if (key == KeyEvent.VK_UP) bombUpPressed = false;
        else if (key == KeyEvent.VK_DOWN) bombDownPressed = false;
        else if (key == KeyEvent.VK_LEFT) bombLeftPressed = false;
        else if (key == KeyEvent.VK_RIGHT) bombRightPressed = false;

        // place/detonate
        else if (key == KeyEvent.VK_J) detonatePressed = false;
        else if (key == KeyEvent.VK_H) placeKeyDown = false;
    }
}

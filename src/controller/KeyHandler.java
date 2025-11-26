package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed, placePressed, detonatePressed,
                    placeKeyDown;


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

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
        else if (key == KeyEvent.VK_H) {
            if (placeKeyDown != true) {
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
        else if (key == KeyEvent.VK_J) {
            detonatePressed = false;
        }
        else if (key == KeyEvent.VK_H) {
            placeKeyDown = false;
        }

    }

}

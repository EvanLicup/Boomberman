package model;

import controller.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * WalkingBomb: controllable via arrow keys (KeyHandler).
 * Behaves like the Hero: has hitBox, respects tile collisions and bounds.
 * Explosion is triggered externally (GameModel listens to keyH.detonatePressed)
 */
public class WalkingBomb {
    // pixel position (top-left of sprite)
    private int x;
    private int y;

    // tile coordinates derived from center
    private int tileCol;
    private int tileRow;

    // bounding hitbox (relative to x,y) — tuned similar to Hero/drone
    public Rectangle hitBox = new Rectangle(8, 8, 32, 32);

    private final GameModel gm;
    private final KeyHandler keyH;

    // movement pixels per update (choose similar feel to Hero)
    private final int speed = 4;

    // flags
    private boolean exploded = false;

    // sprites
    public BufferedImage spriteUp, spriteDown, spriteLeft, spriteRight;
    private BufferedImage spriteCurrent;

    public WalkingBomb(int startCol, int startRow, GameModel gm, KeyHandler keyH) {
        this.gm = gm;
        this.keyH = keyH;
        this.tileCol = startCol;
        this.tileRow = startRow;
        this.x = startCol * gm.tileSize;
        this.y = startRow * gm.tileSize;
        // default facing sprite (optional)
        this.spriteCurrent = null;
    }

    public void setSprites(BufferedImage up, BufferedImage down, BufferedImage left, BufferedImage right) {
        this.spriteUp = up;
        this.spriteDown = down;
        this.spriteLeft = left;
        this.spriteRight = right;
    }

    public BufferedImage getCurrentSprite() { return spriteCurrent; }

    public int getTileRow() { return tileRow; }
    public int getTileCol() { return tileCol; }
    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isExploded() { return exploded; }

    public void update() {
        if (exploded) return;
        if (keyH == null || gm == null) return;

        int nextX = x;
        int nextY = y;

        // Arrow keys move the walking bomb (separate from hero)
        if (keyH.bombUpPressed) {
            nextY -= speed;
            if (spriteUp != null) spriteCurrent = spriteUp;
        } else if (keyH.bombDownPressed) {
            nextY += speed;
            if (spriteDown != null) spriteCurrent = spriteDown;
        } else if (keyH.bombLeftPressed) {
            nextX -= speed;
            if (spriteLeft != null) spriteCurrent = spriteLeft;
        } else if (keyH.bombRightPressed) {
            nextX += speed;
            if (spriteRight != null) spriteCurrent = spriteRight;
        } else {
            // no movement keys -> keep current sprite as-is
        }

        // collision check using hitBox corners (tile based)
        if (canMoveTo(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            // If blocked, don't move into the tile (keeps bomb from clipping through walls)
            // Optionally you could zero the velocity or nudge it back to tile-aligned position:
            // snap center back to nearest tile center (optional)
            snapToTileIfClose();
        }

        // clamp to bounds (just in case)
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        int maxX = gm.tiles[0].length * gm.tileSize - gm.tileSize;
        int maxY = gm.tiles.length * gm.tileSize - gm.tileSize;
        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;

        // update tile coordinates based on center
        int centerX = x + hitBox.x + hitBox.width / 2;
        int centerY = y + hitBox.y + hitBox.height / 2;
        tileCol = centerX / gm.tileSize;
        tileRow = centerY / gm.tileSize;
    }

    // Snap the walking bomb to the nearest tile if it's close to center (avoids stuck partial overlap)
    private void snapToTileIfClose() {
        int centerX = x + hitBox.x + hitBox.width / 2;
        int centerY = y + hitBox.y + hitBox.height / 2;
        int col = centerX / gm.tileSize;
        int row = centerY / gm.tileSize;
        int desiredX = col * gm.tileSize - hitBox.x;
        int desiredY = row * gm.tileSize - hitBox.y;

        // if we're within half a tile of the desired center, snap
        int dx = Math.abs(x - desiredX);
        int dy = Math.abs(y - desiredY);
        if (dx < gm.tileSize / 2) x = desiredX;
        if (dy < gm.tileSize / 2) y = desiredY;
    }

    // Checks tile collision for the bounding box positioned at (nextX, nextY).
    // Returns true if the bomb CAN be at that position (all 4 corner tiles walkable or destructible).
    private boolean canMoveTo(int nextX, int nextY) {
    int tileSize = gm.tileSize;

    int left = nextX + hitBox.x;
    int right = nextX + hitBox.x + hitBox.width - 1;
    int top = nextY + hitBox.y;
    int bottom = nextY + hitBox.y + hitBox.height - 1;

    int topRow = top / tileSize;
    int bottomRow = bottom / tileSize;
    int leftCol = left / tileSize;
    int rightCol = right / tileSize;

    // out of bounds -> not movable
    if (topRow < 0 || leftCol < 0 || bottomRow >= gm.tiles.length || rightCol >= gm.tiles[0].length) {
        return false;
    }

    Tile t1 = gm.tiles[topRow][leftCol];
    Tile t2 = gm.tiles[topRow][rightCol];
    Tile t3 = gm.tiles[bottomRow][leftCol];
    Tile t4 = gm.tiles[bottomRow][rightCol];

    if (t1 == null || t2 == null || t3 == null || t4 == null) return false;

    // ONLY allow movement on tiles that return true for isWalkable().
    // This treats destructible tiles ('D'), indestructible, borders, etc. as blocking.
    return t1.isWalkable() && t2.isWalkable() && t3.isWalkable() && t4.isWalkable();
}

    /**
     * Explode the walking bomb (expanded radius: center +/- 2 in cross)
     */
    public void explode() {
        if (exploded) return;
        exploded = true;

        int centerR = tileRow;
        int centerC = tileCol;

        // destroy center and cross up to distance 2 (center +/- 2)
        gm.destroyTile(centerR, centerC);
        gm.destroyTile(centerR + 1, centerC);
        gm.destroyTile(centerR - 1, centerC);
        gm.destroyTile(centerR, centerC + 1);
        gm.destroyTile(centerR, centerC - 1);

        // extra ring (+1 distance)
        gm.destroyTile(centerR + 2, centerC);
        gm.destroyTile(centerR - 2, centerC);
        gm.destroyTile(centerR, centerC + 2);
        gm.destroyTile(centerR, centerC - 2);

        // damage drones in expanded cross: check rows/cols up to distance 2
        for (int r = centerR - 2; r <= centerR + 2; r++) {
            for (int c = centerC - 2; c <= centerC + 2; c++) {
                if (r == centerR || c == centerC) {
                    gm.checkDroneDamageAt(r, c);
                }
            }
        }

        // Hero damage: if hero currently sits in affected cross (distance <=2 along row or column) -> delegate
        int hr = gm.hero.getTileRow();
        int hc = gm.hero.getTileCol();
        boolean heroHit = (hr == centerR && Math.abs(hc - centerC) <= 2) || (hc == centerC && Math.abs(hr - centerR) <= 2);
        if (heroHit) {
            gm.handleHeroDeath();
        }
    }
}

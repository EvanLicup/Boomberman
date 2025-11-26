package model;

/**
 * FastDrone: same behaviour as drone but 20% faster and uses a separate sprite set.
 */
public class FastDrone extends drone {

    /**
     * Constructs a FastDrone at the specified tile coordinates and applies +20% speed.
     * Uses same GameModel as drone.
     */
    public FastDrone(int tileCol, int tileRow, GameModel gm) {
        super(tileCol, tileRow, gm);
        // increase speed by 20% (round to nearest int, ensure at least 1)
        int newSpeed = Math.max(1, (int)Math.round(super.getSpeed() * 1.6));
        super.setSpeed(newSpeed);
    }

    // no other overrides required — GamePanel will inject the appropriate sprites for FastDrone
}

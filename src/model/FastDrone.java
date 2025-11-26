package model;

/**
 * A faster variant of {@link drone}, moving approximately 20% faster and
 * using a different sprite set assigned by {@link GamePanel}.
 * <p>
 * Aside from its increased speed, this class behaves exactly like a normal drone.
 */
public class FastDrone extends drone {

    /**
     * Creates a new FastDrone at the given tile coordinates.
     * <p>
     * After calling the base {@link drone} constructor, this drone's speed
     * is increased by 20% (rounded) while ensuring minimum speed of 1.
     *
     * @param tileCol the tile column where the drone spawns
     * @param tileRow the tile row where the drone spawns
     * @param gm the game model used for tile access and configuration
     */
    public FastDrone(int tileCol, int tileRow, GameModel gm) {
        super(tileCol, tileRow, gm);
        // increase speed by 20% (round to nearest int, ensure at least 1)
        int newSpeed = Math.max(1, (int)Math.round(super.getSpeed() * 1.6));
        super.setSpeed(newSpeed);
    }

    // no other overrides required — GamePanel will inject the appropriate sprites for FastDrone
}

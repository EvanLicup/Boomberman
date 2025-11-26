package model;

/**
 * Represents a tile that blocks movement until it is destroyed by a bomb explosion.
 * <p>
 * Once destroyed, the tile becomes walkable and allows the {@link Hero}
 * (and other entities) to move through it.
 */
public class DestructibleTile extends Tile {

    /** The character representing a destructible tile on the board. */
    private final char type = 'D';

    /** Whether this tile can currently be walked on (initially false). */
    private boolean walkability = false;

    /** Whether this tile has been destroyed by an explosion. */
    private boolean destroyed = false;

    /**
     * Constructs a destructible tile at the specified board position.
     *
     * @param row the tile's row index
     * @param col the tile's column index
     */
    public DestructibleTile(int row, int col) {
        super(row, col);
    }

    /**
     * Returns whether this tile can currently be walked on.
     *
     * @return {@code true} if the tile is walkable,
     *         {@code false} if still intact and blocking movement
     */
    public boolean isWalkable() {
        return walkability;
    }

    /**
     * Sets whether this destructible tile is walkable.
     *
     * @param walkability {@code true} to make the tile walkable,
     *                    {@code false} to make it blocked
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the character used to visually represent this tile on the board.
     *
     * @return the character 'D'
     */
    public char getType() {
        return this.type;
    }

    /**
     * Marks this tile as destroyed.
     * <p>
     * Once destroyed, it should usually become walkable,
     * depending on how {@link GameModel} handles tile replacement.
     */
    @Override
    public void setDestroyed() {
        this.destroyed = true;
    }

    /**
     * Returns whether this tile has already been destroyed.
     *
     * @return {@code true} if destroyed, {@code false} otherwise
     */
    @Override
    public boolean getDestroyedStatus() {
        return this.destroyed;
    }
}

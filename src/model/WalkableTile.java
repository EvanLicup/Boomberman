package model;

/**
 * Represents a tile that the {@code Hero} can freely walk on.
 * <p>
 * This tile type is always non-blocking and is used to represent empty floor space.
 * A walkable tile:
 * <ul>
 *     <li>is always destructible, though destroying it simply keeps it walkable,</li>
 *     <li>can be temporarily set to non-walkable when a bomb occupies it,</li>
 *     <li>uses the space character (' ') to represent open floor in rendering.</li>
 * </ul>
 * <p>
 * In traditional Bomberman mechanics, when a bomb is placed on a walkable tile,
 * the hero cannot immediately step back onto that tile until the bomb explodes.
 * This behavior is implemented via the {@link #setWalkable(boolean)} method.
 */
public class WalkableTile extends Tile {

    /**
     * The character used to visually represent a walkable tile on the board.
     * A space character (' ') indicates open floor.
     */
    public final char type = ' ';

    /**
     * Whether this tile is currently walkable.
     * <p>
     * This starts as {@code true}, but may temporarily be set to {@code false}
     * when a bomb occupies this tile.
     */
    private boolean walkability = true;

    /**
     * Constructs a walkable tile at the given board position.
     *
     * @param row the tile's row index on the grid
     * @param col the tile's column index on the grid
     */
    public WalkableTile(int row, int col) {
        super(row, col);
    }

    /**
     * Indicates whether this tile is currently walkable by the hero or other entities.
     *
     * @return {@code true} if the tile allows movement; {@code false} otherwise
     */
    public boolean isWalkable() {
        return walkability;
    }

    /**
     * Updates whether this tile should be considered walkable.
     * <p>
     * This is used when bombs temporarily block entry to the tile.
     *
     * @param walkability {@code true} to make the tile walkable again;
     *                    {@code false} to block movement temporarily
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the character representing this tile's type.
     *
     * @return a space character (' '), representing open floor
     */
    public char getType() {
        return type;
    }
}

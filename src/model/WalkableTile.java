package model;

/**
 * Represents a tile that the main.Hero can walk on.
 * This tile is always destructible and initially walkable.
 * <p>
 * In the context of bomberman-style mechanics, a walkable tile may be
 * temporarily set to non-walkable when a bomb is placed on top of it,
 * preventing the main.Hero from occupying the same tile until the bomb explodes.
 * After the explosion, it becomes walkable again.
 */
public class WalkableTile extends Tile{
    /**
     * The character used to visually represent a walkable tile on the board.
     * A space character (' ') is used to indicate open floor.
     */
    public final char type = ' ';

    /**
     * Indicates whether this tile can be walked on.
     * Initially set to {@code true} but may be temporarily changed to {@code false}
     * in the instance a bomb is placed upon it
     */
    private boolean walkability = true;

    /**
     * Constructs a walkable tile at the specified board position.
     *
     * @param row the row index of this tile
     * @param col the column index of this tile
     */
    public WalkableTile(int row, int col) {
        super(row, col);
    }

    // Get info if tile is walkable and destructible
    /**
     * Indicates whether this tile can be walked on by the main.Hero.
     *
     * @return true if the tile allows movement, false otherwise
     */
    public boolean isWalkable() {
        return walkability;
    }

    /**
     * Updates the walkability of this tile.
     *
     * <p>This is commonly used when a bomb is placed on the tile. The tile becomes
     * temporarily non-walkable so that the main.Hero cannot move back onto it, mimicking
     * traditional Bomberman movement rules. After the bomb explodes, the tile may
     * be restored to walkable status by using this method and setting back walkability to true.</p>
     *
     * @param walkability true if the tile should become walkable again, false otherwise
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the display character used to represent this tile on the board.
     *
     * @return a space character (' ') indicating a normal walkable tile
     */
    public char getType() {
        return type;
    }
}

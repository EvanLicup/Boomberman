package model;

public class BarrierTile extends Tile{
    private char type;

    @Override
    public void setBarrierType (char inputChar) {
        if (inputChar == 'B') {
            type = 'B';
        }
        else if (inputChar == 'T') {
            type = 'T';
        }
        else if (inputChar == 'L') {
            type = 'L';
        }
        else if (inputChar == 'R') {
            type = 'R';
        }
        else if (inputChar == '1') {
            type = '1';
        }
        else if (inputChar == '2') {
            type = '2';
        }
        else if (inputChar == '3') {
            type = '3';
        }
        else if (inputChar == '4') {
            type = '4';
        }
    }

    private boolean walkability = false;

    public BarrierTile(int row, int col) {
        super(row, col);
    }

    public boolean isWalkable() {
        return walkability;
    }


    /**
     * Updates the walkability of this indestructible tile.
     *
     * @param walkability whether the tile should now be walkable
     */
    public void setWalkable(boolean walkability) {
        this.walkability = walkability;
    }

    /**
     * Returns the display character representing this tile on the board.
     *
     * @return the character 'I'
     */
    public char getType() {
        return type;
    }
}

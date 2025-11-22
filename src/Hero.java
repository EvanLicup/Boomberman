/**
 * Represents the player-controlled hero in the game board.
 * The hero has a position, a limited number of hearts (lives),
 * and can place one active bomb at a time.
 */
public class Hero {
    /** Current row position of the hero on the board. */
    private int x;

    /** Current column position of the hero on the board. */
    private int y;

    /** Character used to visually represent the hero on the board. */
    private char symbol = '@';

    /** Number of hearts (lives) remaining for the hero. */
    private int hearts;

    /** Indicates whether the hero currently has an active bomb placed. */
    private boolean hasActiveBomb = false;


    // ADDED
    private int heroSpeed = 100;


    /**
     * Constructs a Hero object and initializes its starting position and hearts.
     *
     * @param x      the initial row position of the hero
     * @param y      the initial column position of the hero
     * @param hearts the initial number of hearts (lives) the hero starts with
     */
    public Hero(int x, int y, int hearts) {
        this.x = x;
        this.y = y;
        this.hearts = hearts;
    }


    /**
     * Decrements the hero's heart count by one, typically when hit by an explosion or even an enemy for future development.
     */
    public void loseHeart() {
        this.hearts--;
    }


    /**
     * Returns the current row position of the hero.
     *
     * @return the hero's x-coordinate (row index)
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current column position of the hero.
     *
     * @return the hero's y-coordinate (column index)
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the symbol used to visually represent the hero.
     *
     * @return the character symbol representing the hero
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Validates whether a movement to a given tile is within the bounds
     * of the board and whether that tile is walkable.
     *
     * @param moveX the target row to move to
     * @param moveY the target column to move to
     * @param board the game board used to check boundaries and walkability
     * @return {@code true} if the target tile is inside bounds and walkable, {@code false} otherwise
     */
    public boolean isValidated(int moveX, int moveY, GameBoard board) {
        if (moveX < 0 || moveX >= board.getRows() || moveY < 0 || moveY >= board.getCols()) {
            return false;
        }
        boolean nextMove = board.tileBoard[moveX][moveY].isWalkable();

        if (nextMove == false) {
            return false;
        }

        return true;
    }

    /**
     * Moves the hero based on the key pressed and optionally places a bomb.
     * Movement keys are W, A, S, D (or lowercase variants).
     * Pressing H places a bomb on the current tile if no other bomb is active.
     *
     * @param keyPressed the character representing the player's movement or action input
     * @param board      the current game board used for movement validation
     * @param placedBomb the bomb object to be placed when 'H' is pressed
     *
     * Precondition: No other bomb is currently active when placing a new one.
     */
    public void moveHero(char keyPressed, GameBoard board, Bomb placedBomb) {

        switch (keyPressed) {
            case 'w':
            case 'W':
                if (isValidated(x - 1, y, board)) {
                    x -= 1;
                }
                break;
            case 's':
            case 'S':
                if (isValidated(x + 1, y, board)) {
                    x += 1;
                }
                break;
            case 'a':
            case 'A':
                if (isValidated(x, y - 1, board)) {
                    y -= 1;
                }
                break;
            case 'd':
            case 'D':
                if (isValidated(x, y + 1, board)) {
                    y += 1;
                }
                break;
            case 'h':
            case 'H':


                if (!getActiveBombStatus()) {
                    placedBomb.activate();
                    setHasActiveBomb(true);
                    placedBomb.setRowIndex(x);
                    placedBomb.setColIndex(y);
                    board.tileBoard[x][y].setWalkable(false);
                    /* setWalkable added back and working, setWalkable back to true is put on NormalBomb.explode,
                       so it appears to be simultaneously breaking the nearby D tiles and the bomb disappearing
                     */
                }
                
                
                break;


        }

    }


    /** Updates the hero's active bomb status.
     * @param choice {@code true} if a bomb is now active, {@code false} once the bomb has exploded
     */
    public void setHasActiveBomb(boolean choice) {
        hasActiveBomb = choice;
    }

    /** @return the remaining hearts of the hero */
    public int getHearts() {
        return hearts;
    }

    /** @return {@code true} if a bomb is currently active, {@code false} otherwise */
    public boolean getActiveBombStatus() {
        return hasActiveBomb;
    }

    // ADDED


    public int getHeroSpeed() {
        return heroSpeed;
    }
}
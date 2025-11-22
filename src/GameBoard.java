/**
 * Represents the game board for Bomberman, composed of a 2D array of Tile objects.
 * The board contains destructible, indestructible, and walkable tiles, as well as
 * a designated win position that the hero can reach to complete the level.
 */
public class GameBoard {
    /** Number of rows in the board. */
    private int rows;

    /** Number of columns in the board. */
    private int cols;

    /** 2D array of Tile objects representing the board layout. */
    public Tile[][] tileBoard;

    /** X-coordinate (row) of the win/exit position. */
    private int winCoordinateX = 0;

    /** Y-coordinate (column) of the win/exit position. */
    private int winCoordinateY = 8;

    /**
     * Constructs a GameBoard from a character sample layout.
     * Initializes the tileBoard with appropriate Tile subclasses
     * based on the characters in the sample board.
     *
     * @param sampleBoard a 2D character array representing the initial board layout
     *                    ('D' = destructible, 'I' = indestructible, ' ' = walkable)
     * @param rows        the number of rows in the board
     * @param cols        the number of columns in the board
     * @param tileBoard   the Tile array to populate with Tile objects
     */
    public GameBoard(char[][] sampleBoard, int rows, int cols, Tile[][] tileBoard) {
        this.rows = rows;
        this.cols = cols;
        this.tileBoard = tileBoard;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (sampleBoard[i][j] == 'D') {
                    tileBoard[i][j] = new DestructibleTile(i, j);
                }
                else if (sampleBoard[i][j] == 'I') {
                    tileBoard[i][j] = new IndestructibleTile(i, j);
                }
                else if (sampleBoard[i][j] == ' ') {
                    tileBoard[i][j] = new WalkableTile(i, j);
                }
            }

        }
    }
    /** @return the number of rows in the board */
    public int getRows() {
        return rows;
    }

    /** @return the number of columns in the board */
    public int getCols() {
        return cols;
    }

    /** @return the X-coordinate (row) of the win/exit position */
    public int getWinCoordinateX() {
        return winCoordinateX;
    }

    /** @return the Y-coordinate (column) of the win/exit position */
    public int getWinCoordinateY() {
        return winCoordinateY;
    }

    /**
     * Displays the current state of the board in the console.
     * The display shows:
     * - Hero's position using its symbol
     * - Active bomb as 'B'
     * - Win position as 'E' if not covered by a destructible tile
     * - All other tiles by their type character
     *
     * @param bomberman the hero object to display on the board
     * @param bomb      the bomb object to display if active
     */
    public void displayBoard(Hero bomberman, Bomb bomb) {
        System.out.println("# # # # # # # # # # # # #");
        for (int i = 0; i < tileBoard.length; i++) {
            System.out.print("# "); // use print, not println
            for (int j = 0; j < tileBoard[i].length; j++) {

                if (tileBoard[i][j].getRow() == bomberman.getX() &&
                        tileBoard[i][j].getCol() == bomberman.getY()) {
                    System.out.print(bomberman.getSymbol() + " ");
                } else if (bomb.getIsActive() && tileBoard[i][j].getRow() == bomb.rowIndex && tileBoard[i][j].getCol() == bomb.colIndex) {
                    System.out.print("B ");
                }  else if (i == winCoordinateX && j == winCoordinateY && tileBoard[i][j].getType() != 'D') {
                    System.out.print("E "); 
                } else {
                    System.out.print(tileBoard[i][j].getType() + " ");
                }
                
            }
            System.out.println("#"); // end of row
        }
        System.out.println("# # # # # # # # # # # # #");
    }


    /**
     * Destroys a destructible tile at the specified coordinates.
     * The tile is replaced with a WalkableTile, allowing movement through it.
     *
     * @param row the row index of the tile to destroy
     * @param col the column index of the tile to destroy
     * Precondition: The coordinates must be within the bounds of the board
     */
    public void destroyTile(int row, int col) {

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            if (tileBoard[row][col].getType() == 'D') {
                tileBoard[row][col] = new WalkableTile(row, col);
            }
        }
    }
}
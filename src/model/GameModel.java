package model;

import controller.KeyHandler;

import java.util.ArrayList;

public class GameModel {
    public CollisionChecker cChecker;
    public ArrayList<Bomba> bombs = new ArrayList<>();
    Tile tiles[][];
    final int originalTileSize = 32;
    final int scale = 3;
    KeyHandler keyH;

    public final int tileSize = originalTileSize * scale;
    public Hero hero;

    public GameModel(KeyHandler keyH) {
        this.keyH = keyH;
        this.tiles = new Tile[inputMap.length][inputMap[0].length];
        this.hero = new Hero(this, keyH);
        this.cChecker = new CollisionChecker(this);
        initializeTiles();
        // --- AUTO SPAWN DRONES ---
        spawnDrone(3, 3);   // drone around upper-left walkable area
        spawnDrone(13, 3);  // drone near upper-right walkable area
        spawnDrone(3, 7);   // drone mid-left
        spawnDrone(13, 7);  // drone mid-right
        // -------------------------
    }

    char[][] inputMap = {
            {'1','T','T','T','T','T','T','T','T','T','T','T','T','T','T','T','2'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D','R'},
            {'L',' ','I',' ','I',' ','I',' ','I','D','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ','D',' ',' ',' ',' ',' ',' ',' ',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L','D','D',' ',' ','D',' ',' ',' ','D',' ',' ',' ',' ',' ',' ','R'},
            {'L','D','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I','D','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D','D','R'},
            {'3','B','B','B','B','B','B','B','B','B','B','B','B','B','B','B','4'}
    };

    public void initializeTiles() {

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {

                char c = inputMap[i][j];

                if (c == 'I') {
                    tiles[i][j] = new IndestructibleTile(i, j);
                }
                else if (c == 'D') {
                    tiles[i][j] = new DestructibleTile(i, j);
                }
                else if (c == ' ') {
                    tiles[i][j] = new WalkableTile(i, j);
                }
                else if (c == 'B') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'L') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'R') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == 'T') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '1') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '2') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '3') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else if (c == '4') {
                    tiles[i][j] = new BarrierTile(i, j);
                    tiles[i][j].setBarrierType(c);
                }
                else {
                    System.out.println("Unknown char at (" + i + "," + j + ")");
                    tiles[i][j] = null;
                }
            }
        }
    }



    public void update(double delta) {
        // update player
        hero.update();

        // update drones (if any)
        if (drones != null) {
            for (int i = 0; i < drones.size(); i++) {
                try {
                    drones.get(i).update();
                } catch (Exception e) {
                    // robust: if a drone throws, keep going
                    e.printStackTrace();
                }
            }
        }

        // update bombs
        for (int i = 0; i < bombs.size(); i++) {
            Bomba b = bombs.get(i);
            b.decreaseTime(delta);
        }

        // remove exploded bombs (keeps bombs list clean)
        bombs.removeIf(b -> b.exploded);

    }

    public void destroyTile(int row, int col) {

        if (row >= 0 && row < tiles.length && col >= 0 && col < tiles[row].length) {
            if (tiles[row][col].getType() == 'D') {
                tiles[row][col] = new WalkableTile(row, col);
            }
        }
        System.out.println("Destroyed tile " + row + "," + col);
    }

    // collection for enemy drones (bots)
    public ArrayList<drone> drones = new ArrayList<>();

    /**
     * Convenience helper to spawn a drone at a given tile coordinate (col, row).
     * Tile coordinates are expected (col = column index, row = row index).
     */
    public void spawnDrone(int col, int row) {
        // convert col/row to drone constructor (drone expects tileCol, tileRow)
        drone d = new drone(col, row, this);
        drones.add(d);
    }

}

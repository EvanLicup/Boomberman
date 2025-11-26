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

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public Hero hero;

    public GameModel(KeyHandler keyH) {
        this.keyH = keyH;
        this.tiles = new Tile[inputMap.length][inputMap[0].length];
        this.hero = new Hero(4,3,3,this, keyH);
        this.cChecker = new CollisionChecker(this);
        initializeTiles();
    }

    char[][] inputMap = {
            {'1','T','T','T','T','T','T','T','T','T','T','T','T','T','T','T','2'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','D','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
            {'L',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','R'},
            {'L',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','I',' ','R'},
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
        hero.update();

        for (int i = 0; i < bombs.size(); i++) {
            Bomba b = bombs.get(i);
            b.decreaseTime(delta);
        }

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


}

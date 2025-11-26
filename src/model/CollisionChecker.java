package model;

/**
 * Handles collision detection between the {@link Hero} and the game's tiles.
 * <p>
 * The {@code CollisionChecker} determines whether the hero’s next movement
 * would place them inside a non-walkable tile, and sets the hero's
 * collision flag accordingly.
 */
public class CollisionChecker {

    /** Reference to the game model containing the tile map and settings. */
    GameModel gm;

    /**
     * Creates a new {@code CollisionChecker} associated with a specific game model.
     *
     * @param gm the game model used for tile access and size configuration
     */
    public CollisionChecker(GameModel gm) {
        this.gm = gm;
    }

    /**
     * Checks which tiles the hero is about to move into based on their direction and speed.
     * <p>
     * The method:
     * <ul>
     *   <li>Calculates the hero's hitbox edges</li>
     *   <li>Converts pixel coordinates into tile coordinates</li>
     *   <li>Identifies the two tiles in the hero's movement direction</li>
     *   <li>Sets {@code hero.collision = true} if either tile is not walkable</li>
     * </ul>
     *
     * @param hero the hero whose movement is being evaluated for collisions
     */
    public void checkTile(Hero hero) {
        int heroLeftX = hero.getX() + hero.hitBox.x;
        int heroRightX = hero.getX() + hero.hitBox.x + hero.hitBox.width;
        int heroTopY = hero.getY() + hero.hitBox.y;
        int heroBottomY = hero.getY() + hero.hitBox.y + hero.hitBox.height;

        int tileSize = gm.tileSize;

        // Hero edges in tile coordinates
        int heroLeftCol = heroLeftX / tileSize;
        int heroRightCol = heroRightX / tileSize;
        int heroTopRow = heroTopY / tileSize;
        int heroBottomRow = heroBottomY / tileSize;

        Tile tile1, tile2;

        switch (hero.direction) {
            case "up":
                int nextTopRow = (heroTopY - hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[nextTopRow][heroLeftCol];
                tile2 = gm.tiles[nextTopRow][heroRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "down":
                int nextBottomRow = (heroBottomY + hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[nextBottomRow][heroLeftCol];
                tile2 = gm.tiles[nextBottomRow][heroRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "left":
                int nextLeftCol = (heroLeftX - hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[heroTopRow][nextLeftCol];
                tile2 = gm.tiles[heroBottomRow][nextLeftCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;

            case "right":
                int nextRightCol = (heroRightX + hero.getHeroSpeed()) / tileSize;
                tile1 = gm.tiles[heroTopRow][nextRightCol];
                tile2 = gm.tiles[heroBottomRow][nextRightCol];
                if (!tile1.isWalkable() || !tile2.isWalkable()) hero.collision = true;
                break;
        }
    }
}

package object;

import main.KeyHandler;

public class Bomba {
    public int x;
    public int y;
    private double timeTilExplosion;
    public String placed = "BOMBA PLCAED";

    public boolean exploded = false;


    public Bomba(int x, int y, double timeTilExplosion) {
        this.x = x;
        this.y = y;
        this.timeTilExplosion = timeTilExplosion;
    }

    public void explode() {
        exploded = true;
        System.out.println("Exploded");
    }

    public void decreaseTime (double delta) {
        if (exploded == true) return;
        timeTilExplosion -= delta;
        if (timeTilExplosion <= 0) {
            explode();
        }
    }



}

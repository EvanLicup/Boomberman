package model;

public class PowerUp {

    public enum Type {
        WALKING,      // walking bomb
        RADIUS,       // radius++
        EXTRA_LIFE    // +1 life (or +100 pts)
    }

    public final int row;
    public final int col;
    public final Type type;

    private boolean picked = false;
    private boolean instructionShown = false;

    public PowerUp(int row, int col, Type t) {
        this.row = row;
        this.col = col;
        this.type = t;
    }

    public boolean isPicked() { return picked; }
    public void pick() { picked = true; }

    public boolean isInstructionShown() { return instructionShown; }
    public void setInstructionShown(boolean v) { instructionShown = v; }

    public String getInstructionText() {
        switch (type) {
            case WALKING:
                return "Walking Bomb: Use arrow keys to move it. Press J to detonate.";
            case RADIUS:
                return "Power+1: Bomb radius increased to 2 tiles!";
            case EXTRA_LIFE:
                return "Extra Life: If lives < 3, gain +1. Otherwise, +100 bonus points!";
        }
        return "";
    }
}

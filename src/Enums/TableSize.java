package Enums;

public enum TableSize {
// the size of the game table can be set to 4 different values
// TINY is mailny for debugging, the other 3 for playing
    TINY(4), SMALL(8), MEDIUM(10), BIG(12);
    private int size;

    private TableSize(int s) {
        size = s;
    }

    public int getSize() {
        return size;
    }
}

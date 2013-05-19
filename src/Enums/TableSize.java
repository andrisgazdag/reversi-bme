package Enums;

public enum TableSize {

    SMALL(8), MEDIUM(10), BIG(12);
    
    private int size;

    private TableSize(int s) {
        size = s;
    }

    public int getSize() {
        return size;
    }
}

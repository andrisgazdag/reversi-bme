package Enums;

import java.io.Serializable;

public enum GameLevel implements Serializable {

    EASY(3), NORMAL(2), HARD(1);
    private final int level;

    private GameLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

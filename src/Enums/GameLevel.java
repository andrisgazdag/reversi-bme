package Enums;

import java.io.Serializable;

public enum GameLevel implements Serializable {
// in single player mode, AI can be set to 3 different levels
    EASY(3), NORMAL(2), HARD(1);
    private final int level;

    private GameLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

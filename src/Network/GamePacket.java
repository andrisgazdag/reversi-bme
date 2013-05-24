package Network;

import Enums.Field;
import Enums.GameLevel;
import java.io.Serializable;

public class GamePacket implements Serializable {

    private Field[][] table = null;
    private boolean redIsNext;
    private int[] step = null;
    private GameLevel level;
    private String playerName;

    public GamePacket(Field[][] table, boolean redIsNext, GameLevel level, String playerName) {
        this.table = table;
        this.redIsNext = redIsNext;
        this.level = level;
        this.playerName = playerName;
    }

    public GamePacket(Field[][] table, boolean redIsNext, int[] step) {
        this.table = table;
        this.step = step;
        this.redIsNext = redIsNext;
    }

    public GamePacket(Field[][] table, boolean redIsNext) {
        this.table = table;
        //     this.step=step;
        this.redIsNext = redIsNext;
    }

    public GamePacket(int[] step) {
        //    this.table = table;
        this.step = step;
        //  this.redIsNext=redIsNext;
    }

    public Field[][] getTable() {
        return table;
    }

    public int[] getStep() {
        return step;
    }

    public boolean getRedIsNext() {
        return redIsNext;
    }

    public GameLevel getLevel() {
        return level;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isRedIsNext() {
        return redIsNext;
    }
    
}

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
    private static final long serialVersionUID = 11345173;

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
    
 @Override
    public String toString() {
        return "Red next: " + redIsNext + "tableSize: " + table.length + "table: " + printField();
    }
     
    public String printField() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                sb.append(table[j][i]);
                sb.append("\t");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
}

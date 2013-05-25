package Network;

import Enums.Field;
import Enums.GameLevel;
import java.io.Serializable;

// serializable class, which contains all the necessary info for networking and saving/loading
public class GamePacket implements Serializable {

    // table
    private Field[][] table = null;
    // who is next (which player)
    private boolean redIsNext;
    // a step (the client only sends a step, the server sends the whole state)
    private int[] step = null;
    // level of AI smartness (single player mode)
    private GameLevel level;
    // name
    private String playerName;
    // for serialization needed
    private static final long serialVersionUID = 11345173;

    // ctors
    
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
        this.redIsNext = redIsNext;
    }

    public GamePacket(int[] step) {
        this.step = step;
    }

    // getters
    
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
    
    // toString function for debugging, to see what goes via network
    @Override
    public String toString() {
        return "\nRed next: " + redIsNext + "\nTable: " + printField();
    }

    // print the game table in readable form
    public String printField() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        int size = 0;
        try {
            size = table.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sb.append(table[j][i]);
                    sb.append("\t");
                }
                sb.append("\n");
            }
            // sometimes random NullPtrExceptions occur, maybe concurrent access?
        } catch (NullPointerException ex) {
            System.out.println("NullPtrEx in GamePacket.toString: " + ex.getLocalizedMessage());
        } finally {
            return sb.toString();
        }
    }
}

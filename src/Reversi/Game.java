package Reversi;

import Enums.Field;
import Enums.TableSize;
import java.util.logging.Logger;

public abstract class Game {

    private TableSize tableSize;  // a tábla mérete tableSize x tableSize
    private Field[][] table = null; // a tabla cellai
    public boolean redIsNext = true;

    protected static final Logger LOGGER = Logger.getLogger("Reversi");
    
    public Game() {
    }

    public Game(TableSize tableSize) {
        this.tableSize = tableSize;
        table = new Field[tableSize.getSize()][tableSize.getSize()];
    }

    public Field[][] getTable() {
        return table;
    }
    
    public void setField(int x, int y, Field field) {
        // ide lehetne vedelmet berakni, hogy csak bizonyos esetekben engedje a 
        // meg a cella ertekenek az atallitasat
        table[x][y] = field;
    }

    public TableSize getTableSize() {
        return tableSize;
    }
    
    public int[] calculateScores() {
        int[] scores = {0, 0};
        for (int ii = 0; ii < tableSize.getSize(); ++ii) {
            for (int jj = 0; jj < tableSize.getSize(); ++jj) {
                if (table[ii][jj] == Field.RED) {
                    ++scores[0];
                } else if (table[ii][jj] == Field.BLUE) {
                    ++scores[1];
                }
            }
        }
        return scores;
    }
    
    
}

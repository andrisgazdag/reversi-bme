package Reversi;

import Enums.Field;
import Enums.TableSize;

public abstract class Game {

    private TableSize tableSize;  // a tábla mérete tableSize x tableSize
    private Field[][] table = null; // a tabla cellai 

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
    
}

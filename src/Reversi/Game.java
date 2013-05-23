package Reversi;

import Enums.Field;
import Enums.TableSize;
import java.util.Arrays;
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
        int size=tableSize.getSize();
        table = new Field[size][size];
        //Arrays.fill(table, Field.EMPTY);
        for(Field[] subarray : table) {
        Arrays.fill(subarray, Field.EMPTY);
    }
        
        table[size/2-1][size/2-1]=Field.RED;
        table[size/2][size/2]=Field.RED;
        table[size/2-1][size/2]=Field.BLUE;
        table[size/2][size/2-1]=Field.BLUE;
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
    
    public abstract boolean iteration(int row, int col);
    
     public boolean updateGame(int row, int col, int changes[], boolean red) {
         if (redIsNext != red) {
            return false;
        }
         else {
             redIsNext = !redIsNext;
         }
        Field me = red ? Field.RED : Field.BLUE;
        setField(row, col, me);
        for (int jj = 0; jj < 8; ++jj) {
            for (int ii = 1; ii <= changes[jj+1]; ++ii) {
                setField(row + ii * rowStepTable[jj], col + ii * colStepTable[jj], me);
            }
        }
        if (changes[0] > 0) {
            return true;
        }
        return false;
    }
    
    private int[] rowStepTable = {-1,-1,-1,0,1,1,1,0};
    private int[] colStepTable = {-1,0,1,1,1,0,-1,-1};    

    public int[] isStepValid(int row, int col, boolean red) {
        int size=getTableSize().getSize();
        int changes[] = new int[size+1]; // inisalájzd tu lauter nulls
        //changes[0] = score!!
        if (row < 0 || row > size-1 || col < 0 || col > size-1) {
            return changes;
        }
        Field[][] table = getTable();
        Field enemy = red ? Field.BLUE : Field.RED;
        Field me = red ? Field.RED : Field.BLUE;

        if (table[row][col] != Field.EMPTY) {
            return changes;
        }

        int actRow, actCol;
        Field actField;
        
        for (int jj = 0; jj < 8; ++jj) {
            for (int ii = 1; ii < size; ++ii) {
                actRow = row + ii * rowStepTable[jj];
                actCol = col + ii * colStepTable[jj];
                if (actRow < 0 || actCol < 0 || actRow > size-1 || actCol > size-1) {
                    break;
                }
                actField = table[actRow][actCol];
                if (actField == enemy) {
                    continue;
                }
                if (actField == me) {
                    changes[0] += changes[jj + 1] = ii - 1;
                    break;
                }
                if (actField == Field.EMPTY) {
                    break;
                }
            }
        }
        
        return changes;
    }

//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row - ii;
//            actCol = col - ii;
//            if (actRow < 0 || actCol < 0) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row - ii;
//            actCol = col;
//            if (actRow < 0) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row - ii;
//            actCol = col + ii;
//            if (actRow < 0 || actCol > size-1) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row;
//            actCol = col + ii;
//            if (actCol > size-1) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row + ii;
//            actCol = col + ii;
//            if (actRow > size-1 || actCol > size-1) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//        
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row + ii;
//            actCol = col;
//            if (actRow > size-1) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//        
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row + ii;
//            actCol = col - ii;
//            if (actRow > size-1 || actCol < 0) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
//        
//        for (int ii = 1; ii < size; ++ii) {
//            actRow = row;
//            actCol = col - ii;
//            if (actCol > size-1) {
//                break;
//            }
//            actField = table[actRow][actCol];
//            if (actField == enemy) {
//                continue;
//            }
//            if (actField == me) {
//                changes[0] += changes[1] = ii - 1;
//                break;
//            }
//            if (actField == Field.EMPTY) {
//                break;
//            }
//        }
    
}

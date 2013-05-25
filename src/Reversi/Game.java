package Reversi;

import Enums.Field;
import Enums.TableSize;
import static java.lang.Thread.sleep;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Game extends Thread {

    protected TableSize tableSize;  //TODO a tábla mérete tableSize x tableSize
    protected Field[][] table = null; //TODO a tabla cellai
    protected Controller ctrlr = null;
    protected boolean redIsNext = true;
    protected static final Logger LOGGER = Logger.getLogger("Reversi");
    public boolean userFlag = false;
    public boolean runFlag = true;
    public int[] step = new int[2];
    
    // Possible steps
    private int[] rowStepTable = {-1, -1, -1, 0, 1, 1, 1, 0};
    private int[] colStepTable = {-1, 0, 1, 1, 1, 0, -1, -1};

    public Game() {
        tableSize = null;
    }

    public void setCtrlr(Controller ctrlr) {
        this.ctrlr = ctrlr;
    }

    public Game(TableSize tableSize, Controller ctrlr) {
        this.ctrlr = ctrlr;
        this.tableSize = tableSize;
        int size = tableSize.getSize();
        table = new Field[size][size];

        for (Field[] subarray : table) {
            Arrays.fill(subarray, Field.EMPTY);
        }

        // Set the deafult chips at the table
        table[size / 2 - 1][size / 2 - 1] = Field.RED;
        table[size / 2][size / 2] = Field.RED;
        table[size / 2 - 1][size / 2] = Field.BLUE;
        table[size / 2][size / 2 - 1] = Field.BLUE;
    }

    public Field[][] getTable() {
        return table;
    }

    public void setTable(Field[][] table) {
        this.table = table;
    }

    public void setRedIsNext(boolean redIsNext) {
        this.redIsNext = redIsNext;
    }

    public boolean isRedIsNext() {
        return redIsNext;
    }

    public void setField(int x, int y, Field field) {
        table[x][y] = field;
    }

    public TableSize getTableSize() {
        return tableSize;
    }
    
    protected void waitForUserClick() {
        while (!userFlag) {                 // waiting for user click
            try {                           // controller will set this flag
                sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        userFlag = false;   // resetting user clicked flag back
    }

    protected int[] calculateScores() {

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

    protected boolean updateGame(int row, int col, int changes[], boolean red) {

        Field me = red ? Field.RED : Field.BLUE;
        setField(row, col, me);
        ctrlr.updateView();

        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int jj = 0; jj < 8; ++jj) {
            for (int ii = 1; ii <= changes[jj + 1]; ++ii) {
                setField(row + ii * rowStepTable[jj], col + ii * colStepTable[jj], me);
            }
        }
        if (changes[0] > 0) {
            ctrlr.updateView();
            return true;
        }
        return false;
    }

    //TODO: ezitten azt adja vissza h az adott szinü (red vany nem-red játékos léphet e még validat
    protected boolean canStep(boolean red) {

        int size = tableSize.getSize();
        for (int jj = 0; jj < size; ++jj) {
            for (int ii = 0; ii < size; ++ii) {
                if (isStepValid(jj, ii, red)[0] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void endIfEnd() {

        if (!canStep(true) && !canStep(false)) {
            ctrlr.endGame();
        }
    }

    protected int[] isStepValid(int row, int col, boolean red) {

        int size = getTableSize().getSize();
        int changes[] = new int[9]; //TODO inisalájzd tu lauter nulls

        if (row < 0 || row > size - 1 || col < 0 || col > size - 1) {
            return changes;
        }

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
                if (actRow < 0 || actCol < 0 || actRow > size - 1 || actCol > size - 1) {
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
}
package Reversi;

import Enums.Field;
import Enums.TableSize;
import static java.lang.Thread.sleep;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Game extends Thread {

    // size of the game table
    protected TableSize tableSize;  //TODO a tábla mérete tableSize x tableSize
    // game table itself
    protected Field[][] table = null; //TODO a tabla cellai
    // reference to the controller
    protected Controller ctrlr = null;
    // true if it is Red's turn, false if Blue's
    protected boolean redIsNext = true;
    // logger
    protected static final Logger LOGGER = Logger.getLogger("Reversi");
    // used when waiting for user input.
    // Controller sets (true) if user clicked somewhere on the game table
    public boolean userFlag = false;
    // tells the Game threads, wether they should run.
    // Controller resets (false) when the game ends
    public boolean runFlag = true;
    // variable to store the user's input (step coordinates)
    public int[] step = new int[2];
    
    // Possible steps
    // from a specific field there are 8 possible directions to "move"
    // up-down-left-right and the 4 diagonals
    // theese are coded in this 2 arrays
    private int[] rowStepTable = {-1, -1, -1, 0, 1, 1, 1, 0};
    private int[] colStepTable = {-1, 0, 1, 1, 1, 0, -1, -1};

    // no-param ctor
    public Game() {
        tableSize = null;
    }

    // setter for Ctrlr, ClientGame needs it
    public void setCtrlr(Controller ctrlr) {
        this.ctrlr = ctrlr;
    }

    // ctor
    public Game(TableSize tableSize, Controller ctrlr) {
        // initialize members
        this.ctrlr = ctrlr;
        this.tableSize = tableSize;
        // create game table
        int size = tableSize.getSize();
        table = new Field[size][size];

        // set all fileds to Empty
        for (Field[] subarray : table) {
            Arrays.fill(subarray, Field.EMPTY);
        }

        // Set the deafult chips at the table
        table[size / 2 - 1][size / 2 - 1] = Field.RED;
        table[size / 2][size / 2] = Field.RED;
        table[size / 2 - 1][size / 2] = Field.BLUE;
        table[size / 2][size / 2 - 1] = Field.BLUE;
    }

    // getter for game table
    public Field[][] getTable() {
        return table;
    }

    // setter for game table
    public void setTable(Field[][] table) {
        this.table = table;
    }

    // setter for redIsNext
    public void setRedIsNext(boolean redIsNext) {
        this.redIsNext = redIsNext;
    }

    // getter for redIsNext
    public boolean isRedIsNext() {
        return redIsNext;
    }

    // sets a particular field to the given state
    public void setField(int x, int y, Field field) {
        table[x][y] = field;
    }

    // getter for table size
    public TableSize getTableSize() {
        return tableSize;
    }
    
    // waits until the user clicks a field on the table
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

    // calculates the both players' scores, by iterating through the whole table
    // and counting the chips
    // returns an array containing the 2 scores
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

    // updates the game state based on the step and the calculated changes
    // starting from that field in all directions
    // param: red tells wether you are now red or blue
    protected boolean updateGame(int row, int col, int changes[], boolean red) {
        Field me = red ? Field.RED : Field.BLUE;    // your own color
        setField(row, col, me);     // first we set the clicked field so that
                                    // the user knows the game registered its input
        ctrlr.updateView();         // updating the GUI with this only change

        try {   // sleeping a bit, so that the "twisting" chips twist later
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int jj = 0; jj < 8; ++jj) { // we iterate through all 8 directions
            for (int ii = 1; ii <= changes[jj + 1]; ++ii) {
                // and set the changing fields to our color the changes array accordingly
                setField(row + ii * rowStepTable[jj], col + ii * colStepTable[jj], me);
            }
        }
        if (changes[0] > 0) {       // if there was at least one change
            ctrlr.updateView();     // GUI gets updated
            return true;
        }
        
        // actually the program should never get here. this function should
        // only be called for pre-checked and valid steps
        return false;
    }

    // returns if the by @param red specified user (color) has or has not any valid steps
    protected boolean canStep(boolean red) {
        // iterating through the whole table, and return true if found a valid step
        // valid step means: its score > 0
        int size = tableSize.getSize();
        for (int jj = 0; jj < size; ++jj) {
            for (int ii = 0; ii < size; ++ii) {
                if (isStepValid(jj, ii, red)[0] != 0) {
                    return true;
                }
            }
        }
        // in case the function have not returned yet, there is no valid step
        return false;
    }

    // checks for both users if any of them has a valid step
    // if not, tells the controller to end the game
    protected void endIfEnd() {
        if (!canStep(true) && !canStep(false)) {
            ctrlr.endGame();
        }
    }

    // checks if a step is valid, returns its score, and an array with the
    // number of chips that will flip in all 8 directions
    // @param: red specifies the color of the new chip
    // @param: row, col specifies where to place the new chip
    protected int[] isStepValid(int row, int col, boolean red) {
        int size = getTableSize().getSize();
        int changes[] = new int[9];     // initialized to zeros, so if we dont
                                        // do any changes, it will stay zero

        // if the given position is out of table range, function returns
        if (row < 0 || row > size - 1 || col < 0 || col > size - 1) {
            return changes;
        }
        // in case the step is not on an emty field, we return
        if (table[row][col] != Field.EMPTY) {
            return changes;
        }
        // return value is in theese cases total zeros, showing the step is not valid

        // depending on param red, we set up what color are we, what the enemy
        Field enemy = red ? Field.BLUE : Field.RED;
        Field me = red ? Field.RED : Field.BLUE;
        
        // variables for the loop
        int actRow, actCol;     // actual field position
        Field actField;         // actual field color

        for (int jj = 0; jj < 8; ++jj) {    // iterating through the 8 directions
            for (int ii = 1; ii < size; ++ii) { // max size-1 iteration in each dir
                // actual position: stepTables spedify the direction, ii the distance
                actRow = row + ii * rowStepTable[jj];
                actCol = col + ii * colStepTable[jj];
                // in case we moved off the table, and havent finished analysing this dir
                // then in this dir there won't be flipping chips, so --> next dir
                if (actRow < 0 || actCol < 0 || actRow > size - 1 || actCol > size - 1) {
                    break;
                }
                // if positions are on the table, set field color
                actField = table[actRow][actCol];
                if (actField == enemy) {    // if found an enemy field
                    continue;               // thats ok, go on
                }
                if (actField == me) {       // if found a self-colored field
                    // add to score how far we've got (how many enemy fields are in the way)
                    changes[0] += changes[jj + 1] = ii - 1;
                    break;                  // this dir finisher --> next dir
                }
                if (actField == Field.EMPTY) { // if found an empty field
                    break; // in this dir there won't be flipping chips, so --> next dir
                }
            }
        }
        // at the end return the changes, which may be a set of zeros (invalid step)
        // or a valid total score at [0], and counts how many chip to flip in the
        // 8 directions (at [1]..[8])
        return changes;
    }
}
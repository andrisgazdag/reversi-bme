package Reversi;

import Enums.GameLevel;

// "Artificial Intelligence" - play against the computer
public class AI {

    private GameLevel level;    // GameLevel stores how smart AI is
    private Game game;          // it needs a reference to the Game object to be able
                                // to use its functions when calculating its next step

    // ctor
    public AI(GameLevel level, Game game) {
        this.level = level;
        this.game = game;
    }
    
        /**
     * @return the level
     */
    public GameLevel getAiLevel() {
        return level;
    }

    // next step function
    // returns an array containing the 2 coordinates of the AI's next step
    // it always calculates the 1. 2. and 3. (currently) best steps, and then
    // selects the one specified by the level.
    // in case there is less than level valid steps, it returns the worst but valid step
    // if there is no valid step, it returns {-1, -1}
    public int[] step() {

        int optimals[][] = new int[3][2];   // 1. 2. and 3. best steps
        int[] max = {0, 0, 0};              // 1. 2. and 3. best steps' scores
        int size = game.getTableSize().getSize();
        for (int jj = 0; jj < size; ++jj) {             // we iterate through 
            for (int ii = 0; ii < size; ++ii) {         // all fields on the table
                int[] actPos = {jj, ii};
                int score = game.isStepValid(jj, ii, false)[0]; // get the score of that step
                if (score > max[0]) {                   // manage the 3 bests
                    max[2] = max[1];
                    max[1] = max[0];
                    max[0] = score;
                    optimals[2] = optimals[1];
                    optimals[1] = optimals[0];
                    optimals[0] = actPos;
                } else if (score > max[1]) {
                    max[2] = max[1];
                    max[1] = score;
                    optimals[2] = optimals[1];
                    optimals[1] = actPos;
                } else if (score > max[2]) {
                    max[2] = score;
                    optimals[2] = actPos;
                }
            }
        }
        // in case there is less than level valid steps, it returns the worst but valid step
        for (int kk = 0; kk < level.getLevel(); ++kk) {
            if (max[level.getLevel() - 1 - kk] != 0) {
                return optimals[level.getLevel() - 1 - kk];
            }
        }
        // if there is no valid step, it returns {-1, -1}
        int[] ret = {-1, -1};
        return ret;
    }
}
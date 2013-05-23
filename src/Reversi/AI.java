/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Enums.GameLevel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Satrafuckar
 */
public class AI {
    private GameLevel level;
    private Game game;
//    private Controller ctrlr;
    
    public AI(GameLevel level, Game game/*, Controller ctrlr*/){
        this.level=level;
        this.game=game;
//        this.ctrlr=ctrlr;
    }

    public int[] step() {
               
        int optimals[][] = new int[3][2];
        int[] max = {0, 0, 0};
        int size = game.getTableSize().getSize();
        for (int jj = 0; jj < size; ++jj) {
            for (int ii = 0; ii < size; ++ii) {
                int[] actPos = {jj, ii};
                int score = game.isStepValid(jj, ii, false)[0];
                if (score > max[0]) {
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
        for (int kk = 0; kk < level.getLevel(); ++kk) {
            if (max[level.getLevel() - 1 - kk] != 0) {
                return optimals[level.getLevel() - 1 - kk];
            }
        }
        int[] ret = {-1,-1};
        return ret;
    }
}

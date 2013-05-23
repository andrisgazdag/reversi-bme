/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Enums.GameLevel;

/**
 *
 * @author Satrafuckar
 */
public class AI {
    private GameLevel level;
    private Game game;
    private Controller ctrlr;
    
    public AI(GameLevel level, Game game, Controller ctrlr){
        this.level=level;
        this.game=game;
        this.ctrlr=ctrlr;
    }
            
    public int[] step() {
        int[][] optimals = new int[3][2];
        int max1 = 0, max2 = 0, max3 = 0;
        int size = game.getTableSize().getSize();
        for (int jj = 0; jj < size; ++jj) {
            for (int ii = 0; ii < size; ++ii) {
                int score = ctrlr.isStepValid(jj, ii, false)[0];
                if ( score > max1) {
                    max3 = max2;
                    max2 = max1;
                    max1 = score;
                    optimals[3] = {jj,ii};
                    
                    
                }
                else if (score > max2) {
                }
                else if (score > max3) {
                }

            }
        }

        return optimals[level.getLevel()];
    }
}

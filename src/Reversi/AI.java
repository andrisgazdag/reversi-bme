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
    
    public AI(GameLevel level){
        this.level=level;
    }
            
    public int[] step()
    {
        int[][] optimals=new int[3][2];
        int max1=0, max2=0, max3=0;
        return optimals[level.getLevel()];
    }
}

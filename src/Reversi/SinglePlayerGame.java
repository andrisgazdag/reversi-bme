/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Enums.GameLevel;
import Enums.TableSize;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class SinglePlayerGame extends Game {

    private AI ai;

    public SinglePlayerGame(TableSize size, Controller ctrlr, GameLevel level)
    {
        super(size, ctrlr);
        ai = new AI(level, this);
    }
    
    @Override
     public boolean iteration(int row, int col) // nem bool
    {
        int[] changes = isStepValid(row, col, true); // helyes-e a lepes
        if (changes[0] == 0) {
            LOGGER.log(Level.FINER, "Invalid step");
            return false; // ha nem akkor exit
        } else {
            updateGame(row, col, changes, true); // jatek allapotanak frissitese
            //gameView.repaint(); // gui ujrarajzolasa
//            ctrlr.updateView();
            
            try {
                Thread.sleep(2000); // lassítja az AI válaszát
                LOGGER.log(Level.FINER, "Controller slept...");
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            int[] respAI = ai.step();
            int rowAI = respAI[0];
            int colAI = respAI[1];
            int[] changesAI = isStepValid(rowAI, colAI, false);
            if (changesAI[0] == 0) {
                return false; // AI nem tudott lepni
            } else {
                updateGame(rowAI, colAI, changesAI, false); // AI lepett
//                ctrlr.updateView();
                LOGGER.log(Level.FINER, "Update after AI has stepd.");
                
                //update game user
                //score
                //gui
                //delay

                //ai
                //update game ai
                //update gui //redraw
                //update gui //redraw
            }
        }
        return true;
    }
}

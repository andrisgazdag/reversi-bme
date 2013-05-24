/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reversi;

import Enums.GameLevel;
import Enums.TableSize;
import static Reversi.Game.LOGGER;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alex
 */
public class SinglePlayerGame extends Game {

    private AI ai;
    private GameLevel level;

    public SinglePlayerGame(TableSize size, Controller ctrlr, GameLevel level)
    {
        super(size, ctrlr);
        ai = new AI(level, this);
        this.level = level;
    }
    
    @Override
    public boolean iteration(int row, int col) {
        int[] changes = isStepValid(row, col, true); // helyes-e a lepes
        if (changes[0] == 0) { //ez a lépés nem valid
            LOGGER.log(Level.FINER, "Invalid step");
            if (canStep(true)) { //ha ez nem valid, de lenne valid -->user találja meg
                endIfEnd();
                return false;
            } else { // tehát nincs a usernek valid lépése
                if (!canStep(false)) { // a gépnek sincs
                    ctrlr.endGame(); //vége van
                } else { // AI jöjjön
                    redIsNext = !redIsNext;
                    //redIsNext = false;
                }
            }

        } else { // a lépés valid
            updateGame(row, col, changes, true); // jatek allapotanak frissitese
            try { // AI "gondolkozik"
                Thread.sleep(1000); // lassítja az AI válaszát
                LOGGER.log(Level.FINER, "Controller slept...");
            } catch (InterruptedException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int[] respAI = ai.step();
        int rowAI = respAI[0];
        int colAI = respAI[1];
        int[] changesAI = isStepValid(rowAI, colAI, false);
        if (changesAI[0] == 0) {
            if (canStep(false)) {
                LOGGER.log(Level.FINER, "IMPOSSIBLE!! AI nem tud lepni pedig megis");
            }
            redIsNext = !redIsNext;
            //redIsNext = true;
            endIfEnd();
            return false; // AI nem tudott lepni
        } else {
            updateGame(rowAI, colAI, changesAI, false); // AI lepett
            LOGGER.log(Level.FINER, "Update after AI has stepped.");
            
            while (!canStep(true)) {
                if (!canStep(false)) {
                    ctrlr.endGame();
                    return false;
                }
                respAI = ai.step();
                rowAI = respAI[0];
                colAI = respAI[1];
                changesAI = isStepValid(rowAI, colAI, false);
                updateGame(rowAI, colAI, changesAI, false); // AI lepett
                LOGGER.log(Level.FINER, "Update after AI has stepped.");
            }

            //update game user
            //score
            //gui
            //delay

            //ai
            //update game ai
            //update gui //redraw
            //update gui //redraw
        }
        endIfEnd();
        return true;
    }

    public GameLevel getLevel() {
        return level;
    }
    
    @Override
    public void run() {
        while (runFlag) {
            endIfEnd();
            if (redIsNext && canStep(true)) { // USER lép
                while (!userFlag) {
                    try {
                        sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                userFlag=false;
                int[] changes = isStepValid(step[0], step[1], true); // helyes-e a lepes
                if (changes[0] == 0) { //ez a lépés nem valid
                    continue;
                } else { //ez valid
                    updateGame(step[0], step[1], changes, true); // jatek allapotanak frissitese
                    redIsNext = false;
                }

            } else if (canStep(false)){    // AI lép
                
                try { // AI "gondolkozik"
                    Thread.sleep(1000); // lassítja az AI válaszát
                    LOGGER.log(Level.FINER, "ai slept...");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

                int[] respAI = ai.step();
                int[] changesAI = isStepValid(respAI[0], respAI[1], false);
                if (changesAI[0] != 0) {
                    updateGame(respAI[0], respAI[1], changesAI, false);
                }
                redIsNext=true;
            }
        }
    }
}



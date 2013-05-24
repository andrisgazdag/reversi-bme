package Reversi;

import Enums.GameLevel;
import Enums.TableSize;
import static Reversi.Game.LOGGER;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SinglePlayerGame extends Game {

    private AI ai;
    private GameLevel level;

    public SinglePlayerGame(TableSize size, Controller ctrlr, GameLevel level) {
        super(size, ctrlr);
        ai = new AI(level, this);
        this.level = level;
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
                userFlag = false;
                int[] changes = isStepValid(step[0], step[1], true); // helyes-e a lepes
                if (changes[0] == 0) { //ez a lépés nem valid
                    continue;
                } else { //ez valid
                    updateGame(step[0], step[1], changes, true); // jatek allapotanak frissitese
                    redIsNext = false;
                }

            } else if (canStep(false)) {    // AI lép

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
                redIsNext = true;
            }
            
        }
        
    }
}

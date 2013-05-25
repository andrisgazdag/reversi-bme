package Reversi;

import Enums.GameLevel;
import Enums.TableSize;
import static Reversi.Game.LOGGER;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

// Single player (local) Game Thread (playing vs AI)
public class SinglePlayerGame extends Game {

    // Artificial intelligence member
    private AI ai;
    // AI's level - no need to store here, it is in AI
    // private GameLevel level;

    public SinglePlayerGame(TableSize size, Controller ctrlr, GameLevel level) {
        super(size, ctrlr);
        ai = new AI(level, this);
        //   this.level = level;
    }

    /**
     * @return the ai
     */
    public AI getAi() {
        return ai;
    }

//    public GameLevel getLevel() {
//        return level;
//    }
    
    // main function of the SinglePlayer Thread
    @Override
    public void run() { 
        while (runFlag) {   // the game runs until the Controller sets this flag to false
            endIfEnd();     // check if there is a possible valid step
                            // if there's none, end the Game
            if (redIsNext && canStep(true)) {   // User's turn, and the user CAN step valid
                waitForUserClick();             // waiting for user input
                // check if the step the user clicked is valid
                int[] changes = isStepValid(step[0], step[1], true);
                if (changes[0] == 0) {          // it is not valid
                    continue;       // go back and wait for a next (valid) step
                } else {                        // the current step is valid
                    // update Game state according to the current step
                    updateGame(step[0], step[1], changes, true);
                    if (canStep(false)) {   // in case the AI has valid step
                        redIsNext = false;  // AI is next
                    }
                }
            } else if (canStep(false)) {    // if the AI can step valid
                try {                       // AI "thinks"
                    Thread.sleep(1000);
                    LOGGER.log(Level.FINER, "AI thinking...");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                int[] respAI = getAi().step();  // AI says what to step
                // calculating what changes because of this step
                int[] changesAI = isStepValid(respAI[0], respAI[1], false);
                if (changesAI[0] != 0) {    // in case the AI could step valid
                    // updating the Game state accordingly
                    updateGame(respAI[0], respAI[1], changesAI, false);
                }
                // set that User is next. In case user cannot step valid, AI will step anyway
                redIsNext = true;
            }
        }      
    }
}

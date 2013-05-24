package Reversi;

import Enums.Field;
import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;
import static Reversi.Game.LOGGER;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGame extends Game {

    NetworkCommunicator nc;
    
    public ServerGame(TableSize tableSize, Controller ctrlr) {
        super(tableSize, ctrlr);
        nc = ctrlr.getNetworkCommunicator();
    }
    
     
    @Override
    public boolean iteration(int row, int col)
    {
        GamePacket gp;
        NetworkPacket np;
        if (!redIsNext) {
            return false;
        }
        int[] changes = isStepValid(row, col, true); // helyes-e a lepes
        if (changes[0] == 0) { //ez a lépés nem valid
            LOGGER.log(Level.FINER, "Invalid step");
            if (canStep(true)) { //ha ez nem valid, de lenne valid -->user találja meg
//                endIfEnd();
                return false;
            } else { // tehát nincs a usernek valid lépése
              endIfEnd();
              redIsNext=false;
              gp = new GamePacket(table,redIsNext);
              np = new NetworkPacket(gp);
              nc.send(np);
            }
            
        } else { // a lepes valid
            updateGame(row, col, changes, true); // jatek allapotanak frissitese
        }
        
        np = nc.recive();
        int[] step = new int[2];
        step = ((GamePacket)np.getInfo()).getStep();
        if (step[0]==-1 && step[1]==-1){ // a masiknak nincs valid lepese
            redIsNext=true;
            endIfEnd();
        } else { // valid lepes jott
            changes = isStepValid(step[0], step[1], false);
            updateGame(step[0], step[1], changes, false); // jatek allapotanak frissitese
            
        }
         
        gp = new GamePacket(table,redIsNext);
              np = new NetworkPacket(gp);
              nc.send(np);
        return true;
    }
   
}

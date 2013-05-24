package Reversi;

import Enums.Field;
import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;
import static Reversi.Game.LOGGER;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGame extends Game {

    NetworkCommunicator nc;
    
    public ServerGame(TableSize tableSize, Controller ctrlr) {
        super(tableSize, ctrlr);
        nc = ctrlr.getNetworkCommunicator();
//        GamePacket gp = new GamePacket(table,redIsNext);
//             NetworkPacket np = new NetworkPacket(gp);
//             System.out.println("sent:" + gp);
//              nc.send(np);
        while (!nc.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
            }
        }
        send();
    }
    
//         @Override
//    public boolean iteration(int row, int col) {
//        GamePacket gp;
//        NetworkPacket np;
//        if (!redIsNext) {
//            return false;
//        }
//        int[] changes = isStepValid(row, col, true); // helyes-e a lepes
//        if (changes[0] == 0) { //ez a lépés nem valid
//            LOGGER.log(Level.FINER, "Invalid step");
//            if (canStep(true)) { //ha ez nem valid, de lenne valid -->user találja meg
////                endIfEnd();
//                return false;
//            } else { // tehát nincs a usernek valid lépése
//                endIfEnd();
//                redIsNext = false;
//                send();
//            }
//
//        } else { // a lepes valid
//            updateGame(row, col, changes, true); // jatek allapotanak frissitese
//            send();
//
//        }
//
//        np = nc.recive();
//        System.out.println("recieved:" + (GamePacket) np.getInfo());
//        int[] step;
//        step = ((GamePacket) np.getInfo()).getStep();
//        if (step[0] == -1 && step[1] == -1) { // a masiknak nincs valid lepese
//            redIsNext = true;
//            endIfEnd();
//        } else { // valid lepes jott
//            changes = isStepValid(step[0], step[1], false);
//            updateGame(step[0], step[1], changes, false); // jatek allapotanak frissitese
//
//        }
//
//        send();
//
//        return true;
//    }
         
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
                    if (canStep(false)) {
                        redIsNext = false;
                    }
                    send();//send();
                }
            } else if (canStep(false)) {    // client lép
                recieve();
                int[] changes = isStepValid(step[0], step[1], false);
                updateGame(step[0], step[1], changes, false); // jatek allapotanak frissitese
                if (canStep(true)) {
                    redIsNext = true;
                }
                send();//send();
            }
        }
    }
         

    private void send() {
        GamePacket gp = new GamePacket(table, redIsNext);
        NetworkPacket np = new NetworkPacket(gp);
        System.out.println("sent:" + gp);
//        nc.send(np);
        nc.send_gp(gp);
//        ctrlr.updateView();
    }
    
    private void recieve()
    {
     //  NetworkPacket np = nc.recive();
          GamePacket gp = nc.recive_gp();
        //System.out.println("recieved:" + (GamePacket) np.getInfo());
          System.out.println("recieved:" + gp);
 //       int[] step;
        step = gp.getStep();
    }
}

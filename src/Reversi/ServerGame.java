package Reversi;

import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerGame extends Game {

    NetworkCommunicator nc;

    public ServerGame(TableSize tableSize, Controller ctrlr) {

        super(tableSize, ctrlr);
        nc = ctrlr.getNetworkCommunicator();

        while (!nc.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
            }
        }
        send();

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
                    if (canStep(false)) {
                        redIsNext = false;
                    }
                    send();
                }
            } else if (canStep(false)) {    // client lép
                recieve();
                int[] changes = isStepValid(step[0], step[1], false);
                updateGame(step[0], step[1], changes, false); // jatek allapotanak frissitese
                if (canStep(true)) {
                    redIsNext = true;
                }
                send();
            }
        }
    }

    private void send() {

        GamePacket gp = new GamePacket(table, redIsNext);
        NetworkPacket np = new NetworkPacket(gp);
        System.out.println("sent:" + gp);

        nc.send_gp(gp);

    }

    private void recieve() {

        GamePacket gp = nc.recive_gp();

        System.out.println("recieved:" + gp);
        step = gp.getStep();

    }
}

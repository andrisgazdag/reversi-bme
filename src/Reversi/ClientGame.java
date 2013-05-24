package Reversi;

import Enums.Field;
import Enums.ReversiType;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;
import static Reversi.Game.LOGGER;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGame extends Game {

    private Network.NetworkCommunicator nC = null;

    public ClientGame(String choosenServer, Controller ctrlr) {
        super(); // table size should be set later, ones the server has sent the information

        // create and start the network communicator
        ReversiType gameMode = ReversiType.CLIENT;

        nC = new NetworkCommunicator(gameMode, ctrlr);
        nC.start();

        nC.connectToGame(choosenServer);

        // else sleep a little bit, than try again...
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
        }

        // wait until the communications are established
        while (!nC.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
            }
        }


        //server-töl recieve és kirajzol alapállapot
    }

    @Override
    public boolean iteration(int row, int col) {
        GamePacket packet;
        NetworkPacket np;
        if (redIsNext) {
            return false;
        }
        int[] changes = isStepValid(row, col, true); // helyes-e a lepes
        if (changes[0] == 0) { //ez a lépés nem valid
            LOGGER.log(Level.FINER, "Invalid step");
            if (canStep(true)) { //ha ez nem valid, de lenne valid -->user találja meg
                endIfEnd();
                return false;
            } else { // tehát nincs a usernek valid lépése
                int[] step = {-1, -1};
                packet = new GamePacket(step);
                np = new NetworkPacket(packet);
                nC.send(np);
            }
        } else {
            int[] step = {row, col};
            packet = new GamePacket(step);
            np = new NetworkPacket(packet);
            nC.send(np);
            setField(row, col, Field.BLUE);
            ctrlr.updateView();
        }

        //recieve and draw

        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        recieve();

        return true;
    }

    private void recieve() {
        //NetworkPacket recieved = nC.recive();
        //table = recieved.getInfo().getTable();
        //redIsNext = recieved.getRedIsNext();
    }
}
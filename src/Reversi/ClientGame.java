package Reversi;

import Enums.Field;
import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkPacket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGame extends Game {

    private Network.NetworkCommunicator nC;

    public ClientGame(String choosenServer, Controller ctrlr) {

        super(); // table size should be set later, ones the server has sent the information
        super.setCtrlr(ctrlr);

        // Get the networkCommunicator
        nC = ctrlr.getNetworkCommunicator();
        System.out.println("got nc");
        nC.connectToGame(choosenServer);
        System.out.println("connected nc: " + choosenServer);

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
        System.out.println("while utan --> isconnected nc");

        recieve();

    }

    @Override
    public void run() {

        while (runFlag) {

            endIfEnd();
            if (!redIsNext && canStep(false)) { // USER lép (client)
                while (!userFlag) {
                    try {
                        sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                userFlag = false;
                int[] changes = isStepValid(step[0], step[1], false); // helyes-e a lepes
                if (changes[0] == 0) { //ez a lépés nem valid
                    continue;
                } else { //ez valid
                    send();
                    redIsNext = true;
                }
            }
            if (redIsNext) {
                recieve();
                ctrlr.updateView();
                userFlag = false;
            }

        }

    }

    private void recieve() {   //recieve and draw

        GamePacket gp = nC.recive_gp();
        System.out.println("recieved:" + gp.toString());
        table = gp.getTable();
        redIsNext = gp.getRedIsNext();
        if (tableSize == null) {
            switch (table.length) {
                case 8:
                    tableSize = TableSize.SMALL;
                    break;
                case 10:
                    tableSize = TableSize.MEDIUM;
                    break;
                case 12:
                    tableSize = TableSize.BIG;
                    break;
                default:
                    tableSize = TableSize.TINY;
            }
        }

    }

    private void send() {

        GamePacket gp = new GamePacket(step);
        NetworkPacket np = new NetworkPacket(gp);
        System.out.println("sent:" + gp);
        nC.send_gp(gp);
        setField(step[0], step[1], Field.BLUE);
        ctrlr.updateView();

    }
    
}
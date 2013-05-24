package Reversi;

import Enums.Field;
import Enums.ReversiType;
import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkPacket;
import static Reversi.Game.LOGGER;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGame extends Game {

    private Network.NetworkCommunicator nC;

    public ClientGame(String choosenServer, Controller ctrlr) {
        super(); // table size should be set later, ones the server has sent the information
        super.setCtrlr(ctrlr);
        // create and start the network communicator
        //     ReversiType gameMode = ReversiType.CLIENT;

//        nC = new NetworkCommunicator(gameMode, ctrlr);
//        nC.start();

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
        //server-töl recieve és kirajzol alapállapot

        recieve();
//        System.out.println("recieved \n");
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
                userFlag=false;
                int[] changes = isStepValid(step[0], step[1], false); // helyes-e a lepes
                if (changes[0] == 0) { //ez a lépés nem valid
                    continue;
                } else { //ez valid
                    //updateGame(step[0], step[1], changes, false); // jatek allapotanak frissitese
                    //redIsNext = true;
                    send();
                    redIsNext = true;
                }
            } /*else*/ if (redIsNext) {
                recieve(); //recieve();
                ctrlr.updateView();
                 userFlag=false;
             }
         }   
     }
    
//    @Override
//    public boolean iteration(int row, int col) {
//        GamePacket packet;
//        NetworkPacket np;
//        if (redIsNext) {
//            recieve();
//            ctrlr.updateView();
//            return false;
//        }
//        int[] changes = isStepValid(row, col, false); // helyes-e a lepes
//        if (changes[0] == 0) { //ez a lépés nem valid
//            LOGGER.log(Level.FINER, "Invalid step");
//            if (canStep(false)) { //ha ez nem valid, de lenne valid -->user találja meg
////                endIfEnd();
//                return false;
//            } else { // tehát nincs a usernek valid lépése
//                int[] step = {-1, -1};
//                packet = new GamePacket(step);
//                np = new NetworkPacket(packet);
//                System.out.println("sent:" + packet);
//                nC.send(np);
//            }
//        } else {
//            int[] step = {row, col};
//            packet = new GamePacket(step);
//            np = new NetworkPacket(packet);
//            System.out.println("sent:" + packet);
//            nC.send(np);
//            setField(row, col, Field.BLUE);
//            ctrlr.updateView();
//        }
//  
//        endIfEnd();
//        recieve();  //recieve and draw
//        ctrlr.updateView();
//        endIfEnd();
//        
//        return true;
//    }

    private void recieve() {   //recieve and draw
//        NetworkPacket recieved = nC.recive();
//        GamePacket gp = (GamePacket) recieved.getInfo();
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
    
    private void send()
    {
        GamePacket gp = new GamePacket(step);
            NetworkPacket np = new NetworkPacket(gp);
            System.out.println("sent:" + gp);
//            nC.send(np);
            nC.send_gp(gp);
            setField(step[0], step[1], Field.BLUE);
            ctrlr.updateView();
    }
}
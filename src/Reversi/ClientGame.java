package Reversi;

import Enums.Field;
import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkPacket;
import java.util.logging.Level;
import java.util.logging.Logger;


// client game in multiplayer (network) mode
public class ClientGame extends Game {

    // it needs a NetworkCommunicator to communicate with the server
    private Network.NetworkCommunicator nC;

    // ctor
    public ClientGame(String choosenServer, Controller ctrlr) {

        super(); // table size should be set later, once the server has sent the information
        super.setCtrlr(ctrlr); // set the Controller reference

        // Get the networkCommunicator
        nC = ctrlr.getNetworkCommunicator();
        System.out.println("got nc");           // debug info
        nC.connectToGame(choosenServer);        // connect to choosen server
        System.out.println("connected nc: " + choosenServer); // debug info

        // wait a bit for connecting
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
        }

        // wait until the connection is established
        while (!nC.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
            }
        }
        System.out.println("after while --> isconnected nc"); // debug info

        // we recieve the initial state of the game, sent by the server
        recieve();
    }

    // main function of the ClientGame Thread
    @Override
    public void run() {

        while (runFlag) {   // the game runs until the Controller sets this flag to false

            endIfEnd();     // check if there is a possible valid step
                            // if there's none, end the Game
            if (!redIsNext && canStep(false)) {     // Client user's turn
                while (!userFlag) {                 // waiting for user click
                    try {
                        sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                userFlag = false;   // setting user clicked flag back
                // check if the step the user clicked is valid
                int[] changes = isStepValid(step[0], step[1], false);
                if (changes[0] == 0) {  // it is not valid
                    continue;           // go back and wait for a next (valid) step
                } else {                // the current step is valid
                    send();             // send the step to the server
                    redIsNext = true;   // so that it direct enters the next if
            // redIsNext will be overwritten by the servers info anyway
            // but at the end of the game it is important to directly update
            // the Game and the View, before the Controller ends the Game
                }
            }
            if (redIsNext) { // server's turn, we wait to recieve the updated Game state
                recieve();
                ctrlr.updateView(); // updating the GUI
                // so that previous (invalid) clicks of the user cannot take effect
                userFlag = false;
            }
        }
    }

    // recieve the current state of the game
    private void recieve() {   
        // recieve GamePacket via NetworkCommunicator
        GamePacket gp = nC.recive_gp();     
        // for debugging print what we've got
        System.out.println("Client recieved: " + gp.toString());
        // set Game's state variables
        table = gp.getTable();
        redIsNext = gp.getRedIsNext();
        // at first run set the size of the table too
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
    
    // sending one step to the server. the server will calculate the new
    // state of the game, and send it back
    private void send() {
        // put the step in a Serializable GamePacket, and send via NetworkCommunicator
        GamePacket gp = new GamePacket(step);
        NetworkPacket np = new NetworkPacket(gp);
        // for debugging print what we've sent
        System.out.println("Client sent:" + gp);
        nC.send_gp(gp);
        // update only one field, where the user just clicked, not the whole table
        setField(step[0], step[1], Field.BLUE);
        // update the view so that the user can see the game detected its click
        ctrlr.updateView();
    }   
}
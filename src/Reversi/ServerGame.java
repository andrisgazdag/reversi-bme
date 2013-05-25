package Reversi;

import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;

// server game in multiplayer (network) mode
public class ServerGame extends Game {

    // it needs a NetworkCommunicator to communicate with the client
    NetworkCommunicator nc;

    // ctor
    public ServerGame(TableSize tableSize, Controller ctrlr) {

        // initialize the Game base
        super(tableSize, ctrlr);
        // Get the networkCommunicator
        nc = ctrlr.getNetworkCommunicator();

        // wait until a client connects to us, and the connection is established
        nc.waitUntilConnected();
        
        // send the initial state of the game to the client
        send();
    }

    // main function of the ServerGame Thread
    // Server is always Red, Client is always Blue. Game begins with Red.
    @Override
    public void run() {
        while (runFlag) {   // the game runs until the Controller sets this flag to false
            endIfEnd();     // check if there is a possible valid step
                            // if there's none, end the Game
            if (redIsNext && canStep(true)) {   // Server user's turn, and the server CAN step valid
                waitForUserClick();             // waiting for user input
                // check if the step the user clicked is valid
                int[] changes = isStepValid(step[0], step[1], true);
                if (changes[0] == 0) {          // it is not valid
                    continue;       // go back and wait for a next (valid) step
                } else {                        // the current step is valid
                    // update Game state according to the current step
                    updateGame(step[0], step[1], changes, true);
                    if (canStep(false)) {   // in case the client has valid step
                        redIsNext = false;  // client is next
                    }
                    send();     // sending the updated Game state to the client
                }
            } else if (canStep(false)) {    // if the client can step valid
                recieve();                  // we wait for a step from the client
                // calculating what changes because of this step
                int[] changes = isStepValid(step[0], step[1], false);
                // updating the Game state accordingly
                updateGame(step[0], step[1], changes, false);
                if (canStep(true)) {    // in case the server has valid step
                    redIsNext = true;   // server is next
                }
                send();     // sending the updated Game state back to the client
            }
        }
    }

    // send the state of the Game to the client
    private void send() {
        // creating and filling packet
        GamePacket gp = new GamePacket(table, redIsNext);
        NetworkPacket np = new NetworkPacket(gp);
        // for debugging print what we've sent
        System.out.println("Server sent: " + gp);
        nc.send_gp(gp);
    }

    // recieve one step of the client
    private void recieve() {
        GamePacket gp = nc.recive_gp();
        // for debugging print what we've got
        System.out.println(" Server recieved: " + gp);
        // set the Game's step variable, so the run function can handle the step
        step = gp.getStep();
    }
}

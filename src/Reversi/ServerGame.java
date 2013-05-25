package Reversi;

import Enums.TableSize;
import Network.GamePacket;
import Network.NetworkCommunicator;
import Network.NetworkPacket;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        while (!nc.isConnected()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
            }
        }
        // send the initial state of the game to the client
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
    
    // send the state of the Game to the client
    private void send() {
        // creating and filling packet
        GamePacket gp = new GamePacket(table, redIsNext);
        NetworkPacket np = new NetworkPacket(gp);
        // for debugging print what we'v sent
        System.out.println("Server sent: " + gp);
        nc.send_gp(gp);
    }

    // recieve one step of the client
    private void recieve() {
        GamePacket gp = nc.recive_gp();

        System.out.println(" Server recieved: " + gp);
        step = gp.getStep();

    }
}

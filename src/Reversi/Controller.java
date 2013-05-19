package Reversi;

import Enums.Field;
import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import GUI.GamePlayView;
import GUI.GameTypeView;
import Network.NetworkCommunicator;
import java.io.File;

public class Controller {

    ReversiType gameMode = null;
    GameTypeView gameTypeView;
    GamePlayView gameView;
    NetworkCommunicator networkCommunicator = null;
    Game game = null;

    public Controller() {
        // eloszor a jatekvalaszto ablak
        gameTypeView = new GameTypeView(this);
    }

    public String[] getAvailableServerList() {

        if (networkCommunicator == null) {
            networkCommunicator = new NetworkCommunicator(ReversiType.CLIENT);
        }
        return networkCommunicator.getAvailableGames();

    }

    public void startSingleGame(GameLevel level, TableSize size, String playerName) {

        gameTypeView = null; // release the object
        game = new SinglePlayerGame(size);
        gameView = new GamePlayView(size, this); // start new frame

    }

    public void startServerGame(TableSize size, String serverName, String playerName) {

        gameTypeView = null; // release the object
        game = new ServerGame(size);
        gameView = new GamePlayView(size, this); // start new frame

    }

    public void startClientGame(String plyerName, String choosenServer) {
        
        gameTypeView = null; // release the object
        game = new ClientGame();
        gameView = new GamePlayView(size, this); // start new frame
        
    }

    public void startNetworkCommunicator(ReversiType type, String gameName) {

        if (networkCommunicator != null) {
            if (type == networkCommunicator.getGameType()) {
                return;
            }
        }

        stopNetworkCommunicator();

        networkCommunicator = new NetworkCommunicator(type);

        if (type == ReversiType.SERVER) {
            networkCommunicator.setGameName(gameName);
        }

    }

    public void stopNetworkCommunicator() {
        if (networkCommunicator != null) {
            networkCommunicator.commitSuicide();
            networkCommunicator = null;
        }
    }

    public void loadGame(File file) {
    }

    public Field[][] getGameState() {
        return null;
    }

    public static void main(String[] args) {


        Controller ctrl = new Controller();


        /*

         try {


         // saját code

         System.out.println("What would you like to start? (0=server; 1=client)");
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         gameMode = Integer.parseInt(br.readLine()) == 1 ? ReversiType.CLIENT : ReversiType.SERVER;
         } catch (IOException ex) {
         System.out.println("Exception at input: " + ex.getLocalizedMessage());
         }
         System.out.println("GameMode is: " + gameMode);


         // create and start the network communicator
         Network.NetworkCommunicator nC = new NetworkCommunicator(gameMode);
         nC.start();

         // in client gameMode: get the available servers, and connect to the first one
         if (gameMode == ReversiType.CLIENT) {
         String[] games = null;
         boolean keepContinue = true;
         do {
         games = nC.getAvailableGames();
         if (games[0] != null) {
         // if there is an available game, than connect to it
         keepContinue = false;
         nC.connectToGame(games[0]);
         } else {
         // else sleep a little bit, than try again...
         try {
         Thread.sleep(100);
         } catch (InterruptedException ex) {
         System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
         }
         }
         } while (keepContinue);
         }

         // wait until the communications are established
         while (!nC.isConnected()) {
         try {
         Thread.sleep(10);
         } catch (InterruptedException ex) {
         System.out.println("Thread sleep exception in the main thread: " + ex.getLocalizedMessage());
         }
         }

         // send a packet through the networkCommunicator
         nC.send(new NetworkPacket("test message from controller in mode: " + gameMode));

         // recive a packet through the networkCommunicator
         NetworkPacket recived = nC.recive();
         System.out.println("Message arrived: " + recived.getGameName());

         // stop the networkCommunicator
         nC.endCommunications();
         */
    }
}
// indul a játék
//g = new GamePlayView(TableSize.SMALL, this);
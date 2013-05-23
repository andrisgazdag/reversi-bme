package Reversi;

import Enums.Field;
import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import GUI.GamePlayView;
import GUI.GameTypeView;
import GUI.ServerListView;
import Network.NetworkCommunicator;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger("Reversi");
    ReversiType gameMode = null;
    GameTypeView gameTypeView;
    GamePlayView gameView;
    ServerListView serverView;
    NetworkCommunicator networkCommunicator = null;
    Game game = null;
    String gameName = "Skynet";
//  AI ai;
    
    public Controller() {
        // eloszor a jatekvalaszto ablak
        initLogger();
        gameTypeView = new GameTypeView(this);
    }

    public String[] getAvailableServerList() {

        if (networkCommunicator == null) {
            networkCommunicator = new NetworkCommunicator(ReversiType.CLIENT, this);
        }

        return networkCommunicator.getAvailableGames();

    }

    public void startSingleGame(GameLevel level, TableSize size, String playerName) {
        
        gameTypeView = null; // release the object
        game = new SinglePlayerGame(size, level);
        //ai = new AI(level, game/*, this*/);
        //gameView = new GamePlayView(size, this); // start new frame
        new Thread(new GamePlayView(size, this)).start(); // start gui thread
        //gameView.repaint();
        //gameView.reDraw(/*game.getTable(), game.calculateScores()*/);
    }

    public void startServerGame(TableSize size, String serverName, String playerName) {

        gameTypeView = null; // release the object
        game = new ServerGame(size);
        gameView = new GamePlayView(size, this); // start new frame

    }

    public void startClientGame(String plyerName, String choosenServer) {

        gameTypeView = null; // release the object
        game = new ClientGame();
        gameView = new GamePlayView(game.getTableSize(), this); // start new frame

    }

    public void startNetworkCommunicator(ReversiType type) {

        if (networkCommunicator != null) {
            if (type == networkCommunicator.getGameType()) {
                return;
            }
        }

        stopNetworkCommunicator();

        networkCommunicator = new NetworkCommunicator(type, this);

        networkCommunicator.start();

    }

    private void stopNetworkCommunicator() {
        if (networkCommunicator != null) {
            networkCommunicator.selfDestruction();
            networkCommunicator = null;
        }
    }

    public void loadGame(File file) {
        // ide fogalamam sincs, hogy mit kellene irni...
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Field[][] getGameState() {

        if (game != null) {
            return game.getTable();
        }
        return null;
    }
    public int[] getScores()
    {
       return game.calculateScores();
    }

    public void showServers() {
        startNetworkCommunicator(ReversiType.CLIENT);
        serverView = new ServerListView(this);
    }
    
     public boolean iteration(int row, int col)
     {
     return game.iteration(row, col);
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

    private void initLogger() {
        //Initializing logger:
        
        try {
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
            String logDate = df.format(new Date().getTime());

            // the detailed log
            Handler handler = new FileHandler("logs/Reversi_" + logDate + "_ALL.log"); // logs folder should be created manualy
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.FINEST);
            
            // info log, only the most importatnt things...
            Handler handler_info = new FileHandler("logs/Reversi_" + logDate + "_INFO.log"); // logs folder should be created manualy
            handler_info.setFormatter(new SimpleFormatter());
            handler_info.setLevel(Level.INFO);
            
            LOGGER.addHandler(handler);
            LOGGER.addHandler(handler_info);
            LOGGER.setLevel(Level.FINEST);
        } catch (IOException e) {
            System.err.println("logger initialisation error: " + e.getLocalizedMessage());
        }
    }
}
// indul a játék
//g = new GamePlayView(TableSize.SMALL, this);
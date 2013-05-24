package Reversi;

import Enums.Field;
import Enums.GameLevel;
import Enums.ReversiType;
import Enums.TableSize;
import GUI.GamePlayView;
import GUI.GameTypeView;
import GUI.ServerListView;
import Network.GamePacket;
import Network.NetworkCommunicator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        startReversi(); //azért lett külön fgv-be téve mert új játékot lehet indítani a GUIból
    }

    public void startReversi() {
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
        game = new SinglePlayerGame(size, this, level);
        game.start();
        //ai = new AI(level, game/*, this*/);
        gameView = new GamePlayView(size, this); // start new frame
        new Thread(gameView).start(); // start gui thread

        //gameView.repaint();
        //gameView.updateGamePlayView();
    }

    public void startServerGame(TableSize size, String serverName, String playerName) {

        gameTypeView = null; // release the object
        game = new ServerGame(size, this);
        game.start();
        gameView = new GamePlayView(size, this); // start new frame
        new Thread(gameView).start(); // start gui thread

    }

    public void startClientGame(String plyerName, String choosenServer) {

        gameTypeView = null; // release the object
        game = new ClientGame(choosenServer, this);
        game.start();
        gameView = new GamePlayView(game.getTableSize(), this); // start new frame
        new Thread(gameView).start(); // start gui thread

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

    // save game to file
    public void saveGame(File file) {
        FileOutputStream fout = null;
        try {

            GamePacket savePacket;
            if (game instanceof SinglePlayerGame) {
                SinglePlayerGame singGame = (SinglePlayerGame) game;
                savePacket = new GamePacket(singGame.getTable(), game.isRedIsNext(), singGame.getLevel(), gameName);
                LOGGER.log(Level.FINER, "Saved to file: \n {0}", savePacket);
            } else {
                LOGGER.log(Level.SEVERE, "Only single plyer games can be saved!!!!");
                return;
            }

            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(savePacket);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

    }

    // load game from file
    public void loadGame(File file) {
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        try {
            GamePacket savedGame = null;
            fin = new FileInputStream(file);
            ois = new ObjectInputStream(fin);
            savedGame = (GamePacket) ois.readObject();

            // Controll log
            LOGGER.log(Level.FINER, "Load from file: \n {0}", savedGame);

            TableSize tableSize = null;
            // start the game
            switch (savedGame.getTable().length) {
                case 8:
                    tableSize = TableSize.SMALL;
                    break;
                case 10:
                    tableSize = TableSize.MEDIUM;
                    break;
                case 12:
                    tableSize = TableSize.BIG;
                    break;
            }

            startSingleGame(savedGame.getLevel(), tableSize, gameName);

            game.setTable(savedGame.getTable());
            game.setRedIsNext(savedGame.isRedIsNext());


            updateView();


        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    public NetworkCommunicator getNetworkCommunicator() {
        return networkCommunicator;
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

    public int[] getScores() {
        return game.calculateScores();
    }

    public void showServers() {
        startNetworkCommunicator(ReversiType.CLIENT);
        serverView = new ServerListView(this);
    }

    public boolean iteration(final int row, final int col) {
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                game.iteration(row, col);
//            }
//        };
//        new Thread(r).start();

        game.step = new int[]{row, col};
        game.userFlag = true;

        return true;
    }

    public void updateView() {
        gameView.updateGamePlayView();
    }

    public void endGame() {
        
//        try { //hogy minden update-elodjon //NEMITT A HIBA
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        int[] scores = getScores();
        if ((game instanceof SinglePlayerGame) || (game instanceof ServerGame)) {
            if (scores[0] > scores[1]) {
                gameView.showUserWin();
            } else if (scores[0] < scores[1]) {
                gameView.showUserLoose();
            } else {
                gameView.showUserEven();
            }
        } else if (game instanceof ClientGame) {
            if (scores[0] < scores[1]) {
                gameView.showUserWin();
            } else if (scores[0] > scores[1]) {
                gameView.showUserLoose();
            } else {
                gameView.showUserEven();
            }
        }
        game.runFlag = false;
        stopNetworkCommunicator();
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
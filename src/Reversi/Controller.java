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

// Controls the whole game, connects the GUI with the engine
public class Controller {

    // logger
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    // first view, where the user is able to set up every parameter
    // table size, single-multi, AI level, ...
    private GameTypeView gameTypeView;
    // table view
    private GamePlayView gameView;
    // in client mode, this view lists the available servers, the user can select and connect
    private ServerListView serverView;
    // nC communicates with other nC in network (multiplayer) mode
    private NetworkCommunicator networkCommunicator = null;
    // the game itself, with all its functionality
    private Game game = null;
    // name of the server in network (multiplayer) mode
    private String gameName = "Skynet";

    // ctor
    public Controller() {
        initLogger(); // Initializing the logger for the program
        startReversi(); // starting the game
    }

    // Create Game chooser window
    public final void startReversi() {
        gameTypeView = new GameTypeView(this);
    }

    // in network mode, client lists the servers with its nC
    public String[] getAvailableServerList() {
        if (networkCommunicator == null) { // in case there is no nC yer, let's create one
            networkCommunicator = new NetworkCommunicator(ReversiType.CLIENT, this);
        }
        return networkCommunicator.getAvailableGames();     // return server list
    }

    // when starting a new game, checks if there is a game setup window, and closes/releases it
    // also stops the game thread if it exists
    private void cleanUpExistingGameTypeView() {
        if (gameTypeView != null) {             // at load from GPV it is null
            gameTypeView.dispose();             // close the game setup window
            gameTypeView = null;                // release the gameTypeView object
        }
    }

    private void cleanUpExistingGame() {
        if (game != null) {                     // at load from GPV or at new game
            game.runFlag = false;               // stop existing thread
            iteration(-1, -1);
        }
    }
    
    private void cleanUpExistingServerListView() {
        if (serverView != null) {
            serverView.dispose();             // close the server select window
            serverView = null;                // release the serverListView object
        }
    }

    // single player (vs AI) mode starter
    public void startSingleGame(GameLevel level, TableSize size, String playerName) {
        cleanUpExistingGameTypeView();      // clean up existing gameTypeView
        cleanUpExistingGame();              // and game
        // Create the game object
        game = new SinglePlayerGame(size, this, level);
        game.start();                       // start game thread
        // Create the GUI object
        gameView = new GamePlayView(size, this);
        new Thread(gameView).start();       // start gui thread
    }

    // network mode, server starter
    public void startServerGame(TableSize size, String serverName, String playerName) {
        cleanUpExistingGame();              // clean up existing game
        game = new ServerGame(size, this);      // Create the game object
        game.start();                           // start game thread
        cleanUpExistingGameTypeView();      // clean up existing gameTypeView
        gameView = new GamePlayView(size, this);// Create the GUI object
        new Thread(gameView).start();           // start gui thread
    }

    // network mode, client starter
    public void startClientGame(String plyerName, String choosenServer) {
        cleanUpExistingGameTypeView();      // clean up existing gameTypeView
        cleanUpExistingGame();              // and game
        game = new ClientGame(choosenServer, this); // Create the game object
        game.start();                               // start game thread
        cleanUpExistingServerListView();    // clean up server select window
        gameView = new GamePlayView(game.getTableSize(), this); // Create the GUI object
        new Thread(gameView).start();               // start gui thread
    }

    // starts nC, in the mode specified by the @param type
    public void startNetworkCommunicator(ReversiType type) {
        if (networkCommunicator != null) { // check if there is already a nC running
            if (type == networkCommunicator.getGameType()) {
                return;     // if it is the type needed now, it's ok, return
            }
        }
        stopNetworkCommunicator();  // else stop the current instance
        networkCommunicator = new NetworkCommunicator(type, this);  // create
        networkCommunicator.start();                    // and start a new one
    }

    // stops nC
    public void stopNetworkCommunicator() {
        if (networkCommunicator != null) { // only if it exists
            networkCommunicator.selfDestruction();
            networkCommunicator = null;     // resets the reference to null
        }
    }

    // save game to file specified in @param file
    public void saveGame(File file) {
        FileOutputStream fout = null;   // create output stream
        try {
            GamePacket savePacket;      // create serializable packet 
            // saving works only in singleplayer mode at now
            if (game instanceof SinglePlayerGame) {
                SinglePlayerGame singGame = (SinglePlayerGame) game;
                // filling packet with all the needed infos:
                // table, who_is_next, AI_level, name
                //savePacket = new GamePacket(singGame.getTable(), game.isRedIsNext(), singGame.getLevel(), gameName);
                savePacket = new GamePacket(singGame.getTable(), game.isRedIsNext(), singGame.getAi().getAiLevel(), gameName);
                LOGGER.log(Level.FINER, "Saved to file: \n {0}", savePacket);
            } else {
                LOGGER.log(Level.SEVERE, "Only single plyer games can be saved!!!!");
                return;
            }
            // setting up the outputstream
            fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            // writing the serializable object
            oos.writeObject(savePacket);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {     // even if there was an exception
            try {       // try to close the file stream
                fout.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    // load game from file specified in @param file
    public void loadGame(File file) {
        // create output stream
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        try {       // reading the file with the streams, getting packet
            GamePacket savedGame;
            fin = new FileInputStream(file);
            ois = new ObjectInputStream(fin);
            savedGame = (GamePacket) ois.readObject();
            // Controll log
            LOGGER.log(Level.FINER, "Load from file: \n {0}", savedGame);
            TableSize tableSize = null;
            // finding out and setting table size
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
            // start the game
            startSingleGame(savedGame.getLevel(), tableSize, gameName);
            // setting state
            game.setTable(savedGame.getTable());
            game.setRedIsNext(savedGame.isRedIsNext());
            updateView();       // updating GUI
        } catch (ClassNotFoundException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {     // even if there was an exception
            try {       // try to close the file stream
                fin.close();
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {       // and try to close the object stream
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // getter for nC
    public NetworkCommunicator getNetworkCommunicator() {
        return networkCommunicator;
    }

    // getter for game name
    public String getGameName() {
        return gameName;
    }

    // setter for game name
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    // getter for game table, if there is a valig game instance
    public Field[][] getGameState() {
        if (game != null) {
            return game.getTable();
        }
        return null;
    }

    // has the scores calculated by the game instance
    public int[] getScores() {
        return game.calculateScores();
    }

    // in network client mode, starts the server listing view
    public void showServers() {
        gameTypeView.dispose();                 // close the game setup window
        // and also starts the nC 
        startNetworkCommunicator(ReversiType.CLIENT);
        serverView = new ServerListView(this);
    }

    // if the user clicks somewhere on tha table, it triggers an iteration
    public boolean iteration(final int row, final int col) {
        game.step = new int[]{row, col};    // set the step coordinates
        game.userFlag = true;   // signalise to game thread the user interaction
        return true;
    }

    // tell the GUI that it should update itself, because there was some change
    public void updateView() {
        gameView.updateGamePlayView();
    }

    // ending the game
    public void endGame() {
        int[] scores = getScores();     // first we get the up-to-date scores
        // then tell the user that he won/loose/even, according to game type
        // which determines the color, thus the win/loose
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
        game.runFlag = false;       // signalising the game thread to stop
        stopNetworkCommunicator();  // stopping nC
    }
    
    public void networkDied()
    {
        gameView.showNetworkError();
        game.runFlag=false;
        stopNetworkCommunicator();
    }

    // logger setup
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

    // MAIN
    public static void main(String[] args) {
        // The controller class drives the application
        Controller ctrl = new Controller();
    }
}
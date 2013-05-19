package Network;

import Enums.ReversiType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Batman
 */
public class NetworkCommunicator extends Thread {

    // Type of the current running version of the program
    public ReversiType gameType = null;
    // The logger object
    private static final Logger LOGGER = Logger.getLogger(NetworkCommunicator.class.getName());
    // The socket object of the communication
    private Socket connection = null;
    // IP Address of the communication partner
    private InetAddress partnerAddress = null;
    // Streams for communication
    private ObjectOutputStream out;
    private ObjectInputStream in;
    // flag to stop networkCommunicator
    private boolean communicatorIsNeeded;
    // available games
    private HashMap<String, DatagramPacket> availableGames = new HashMap<>();
    // in client mode it controlls the the search intervallum
    private boolean needToSearchForGames;
    // the game advertiser
    private GameAdvertiser gameAD = null;
    // game name
    private String gameName = null;

    /**
     * C'tor of the NetworkCommunicator class
     *
     * @param gameType The type of the current game (Server/Client)
     */
    public NetworkCommunicator(ReversiType gameType) {

        this.gameType = gameType;
        this.communicatorIsNeeded = true;
        this.needToSearchForGames = true;

        LOGGER.log(Level.INFO, "New Networkcommunicator is created in mode: {0}", gameType);

        //Initializing logger:
        try {
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
            String logDate = df.format(new Date().getTime());

            Handler handler = new FileHandler("logs/NetworkCommunicator_" + logDate + ".log"); // logs folder should be created manualy
            handler.setFormatter(new SimpleFormatter());
            LOGGER.setLevel(Level.FINE);
            LOGGER.addHandler(handler);
        } catch (IOException e) {
            System.out.println("logger initialisation error: " + e.getLocalizedMessage());
        }

    }

    public void commitSuicide() {

        if (gameAD != null) {
            gameAD.stopAdvertise();
        }
        stopSearchingForGames();
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        this.interrupt();
    }

    @Override
    public void run() {

        if (gameType == ReversiType.SERVER) {

            LOGGER.log(Level.INFO, "New server thread is started...");

            // starting the advertiser thread
            gameAD = new GameAdvertiser();
            gameAD.start();
            waitForClient();
            gameAD.stopAdvertise();

        } else if (gameType == ReversiType.CLIENT) {

            LOGGER.log(Level.INFO, "New Client thread is started...");

            // search for games
            searchForGames();

        } else {

            LOGGER.log(Level.SEVERE, "Invalid game-type! Fatal error happened!");
            System.exit(-1);

        }

        // The establishment is done, waiting for sending requests...
        while (communicatorIsNeeded) {
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

    }

    public ReversiType getGameType() {
        return gameType;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    
    public boolean isConnected() {

        return (connection != null) && (out != null) && (in != null);

    }

    public void send(NetworkPacket packet) {

        try {
            // Communicating with the server
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Socket establishment Exception: {0}", e.getLocalizedMessage());
        }

    }

    public NetworkPacket recive() {

        NetworkPacket packet = null;

        try {
            packet = (NetworkPacket) in.readObject();
            LOGGER.log(Level.FINER, "Packet recived: {0}", packet);
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Server could not be reserved: {0}", ex);
        }

        return packet;
    }

    public void endCommunications() {

        communicatorIsNeeded = false;

    }

    public String[] getAvailableGames() {

        String[] a = new String[]{null};
        return availableGames.keySet().toArray(a);

    }

    public boolean connectToGame(String gameName) {

        LOGGER.log(Level.FINE, "Connecting to game: {0}", gameName);

        if (availableGames.containsKey(gameName)) {

            // finish the search
            stopSearchingForGames();
            // get the server address
            partnerAddress = availableGames.get(gameName).getAddress();
            // connect to game
            connectToServer();
        }

        return false;

    }

    private void stopSearchingForGames() {
        needToSearchForGames = false;
        LOGGER.log(Level.FINE, "Searching for games has stopped.");
    }

    private void searchForGames() {
        try {
            MulticastSocket ms = new MulticastSocket(60005);
            InetAddress group = InetAddress.getByName("225.0.0.1");
            ms.joinGroup(group);

            DatagramPacket packet;

            byte[] buf = new byte[1024];
            packet = new DatagramPacket(buf, buf.length);

            while (needToSearchForGames) {
                ms.receive(packet);
                NetworkPacket recivedPacket = deserialisePacket(buf);
                String gameName = recivedPacket.getGameName();

                // if a new game is detected, than add to the queue
                if (!availableGames.containsKey(gameName)) {
                    availableGames.put(gameName, packet);
                    LOGGER.log(Level.INFO, "Adding game to available games: {0}", gameName);
                }
            }

            ms.leaveGroup(group);
            ms.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void waitForClient() {

        // The IP Address of the communication partner
        // InetAddress partnerAddress = null;
        // The Server TCP communication port
        int port = 60000;

        try {

            // Create server socket, and wait for incoming connection
            ServerSocket waitingServerSocket = new ServerSocket(port);
            connection = waitingServerSocket.accept();
            // Get the input and output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Server could not be reserved: {0}", ex);
        }

    }

    private void connectToServer() {

        try {
            // Connect to server at port: 60000
            connection = new Socket(partnerAddress, 60000);
            // Get the input and output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Client could not establish socket connection: {0}", ex);
        }

    }

    private class GameAdvertiser extends Thread {

        private boolean needToAdvertise = true;

        /**
         * Advertises the server game in every 5 second.
         */
        @Override
        public void run() {

            LOGGER.log(Level.FINE, "New GameAdvertiser started...");

            DatagramSocket s;
            byte[] buf;
            DatagramPacket dp;

            try {
                // This port will be used, to send the advertise messages
                s = new DatagramSocket(60006);

                //TODO: ezt ki kell cserélni a helyes információra
                NetworkPacket np = new NetworkPacket("Reversi game available at: " + new Date().getTime());

                buf = serialisePacket(np);

                // Destination address and port
                InetAddress group = InetAddress.getByName("225.0.0.1");
                int groupPort = 60005;

                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, groupPort);

                while (needToAdvertise) {
                    LOGGER.log(Level.FINE, "Sending game info...");
                    s.send(packet);
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, "IOException in advertizement: {0}", ex.getLocalizedMessage());
                    }
                }

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "IOException in advertizement: {0}", ex.getLocalizedMessage());
            }
        }

        /**
         * Serializes a NetworkPacket
         *
         * @param packet the packet, which will be serialized
         * @return a byte array of the serialized packet
         */
        public final byte[] serialisePacket(NetworkPacket packet) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(packet);
                oos.flush();
                return baos.toByteArray();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Serialization problem: {0}", e.getLocalizedMessage());
            }
            return null;
        }

        public void stopAdvertise() {
            needToAdvertise = false;
        }
    }

    private NetworkPacket deserialisePacket(byte[] buf) {

        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(bais);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "ObjectInputStreem error: {0}", e.getLocalizedMessage());
        }

        NetworkPacket recivedPacket = null;

        try {
            recivedPacket = (NetworkPacket) ois.readObject();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Read object error: {0}", e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Class not found: {0}", e.getLocalizedMessage());
        }

        return recivedPacket;
    }
}
package Network;

import Enums.ReversiType;
import Reversi.Controller;
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
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Network Communicator class: handles the connections and the communications
 *
 * @author Maria Buthi
 */
public class NetworkCommunicator extends Thread {

    // Type of the current running version of the program
    public ReversiType gameType = null;
    // The logger object
    private static final Logger LOGGER = Logger.getLogger("Reversi");
    // The socket object of the communication
    private Socket connection = null;
    // IP Address of the communication partner
    private InetAddress partnerAddress = null;
    // Port number of the communication partner
    private int partnerPort;
    // Streams for communication
    private ObjectOutputStream out;
    private ObjectInputStream in;
    // flag to stop networkCommunicator
    private boolean communicatorIsNeeded;
    // available games
    private HashMap<String, GameInfo> availableGames = new HashMap<>();
    // timeout for the games
    private HashMap<String, Long> timeoutOfTheGames = new HashMap<>();
    // in client mode it controlls the the search intervallum
    private boolean needToSearchForGames;
    // the game advertiser
    private GameAdvertiser gameAD = null;
    // the controller
    Controller controller = null;
    // the server socket
    ServerSocket waitingServerSocket = null;
    // server port number
    int port = 60000;

    /**
     * C'tor of the NetworkCommunicator class
     *
     * @param gameType The type of the current game (Server/Client)
     * @param controller the Controller class of the game
     */
    public NetworkCommunicator(ReversiType gameType, Controller controller) {

        this.gameType = gameType;
        this.controller = controller;
        this.communicatorIsNeeded = true;
        this.needToSearchForGames = true;

        LOGGER.log(Level.INFO, "New Networkcommunicator is created in mode: {0}", gameType);

    }

    /**
     * To stop the thread of the networkCommunicator. It closes all awaiting
     * sockets and connections.
     */
    public void selfDestruction() {

        LOGGER.log(Level.INFO, "NetworkCommunicator initiaing self destruction...");

        if (gameAD != null) {
            gameAD.stopAdvertise();
        }
        stopSearchingForGames();
        if (waitingServerSocket != null) {
            try {
                waitingServerSocket.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Server socket could not be closed: {0}", ex);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Clinet connection socket could not be closed: {0}", ex);
            }
        }
        LOGGER.log(Level.INFO, "Destruction done. Continuing with thred interruption...");
        this.interrupt();
    }

    /**
     * The main function of the thread.
     */
    @Override
    public void run() {

        if (gameType == ReversiType.SERVER) {

            LOGGER.log(Level.INFO, "New server thread is started...");

            // starting the advertiser thread
            gameAD = new GameAdvertiser();
            startListening(); // start listening on a port, which is free
            gameAD.start();
            waitForClient(); // wait for client to connect
            gameAD.stopAdvertise();

        } else if (gameType == ReversiType.CLIENT) {

            LOGGER.log(Level.INFO, "New Client thread is started...");

            // search for games
            searchForGames();

        } else {

            LOGGER.log(Level.SEVERE, "Invalid game-type! Fatal error happened!");
            System.exit(-1);

        }

        // The main loop of the thread
        // The establishment is done, waiting for sending requests...
        while (communicatorIsNeeded) {

            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, "Sleep interrupted: {0}", ex);
            }

        }

    }

    /**
     * @return Returns the operating mode of the networkCommunicator
     */
    public ReversiType getGameType() {
        return gameType;
    }

    /**
     * @return A boolean value, weather the nC is connected to a communication
     * partner or not.
     */
    public boolean isConnected() {
        return (connection != null) && (out != null) && (in != null);
    }

    /**
     * Sends a packet to the communication partner.
     *
     * @param packet a NetworkPacket
     */
    public void send(NetworkPacket packet) {

        try {
            // Communicating with the server
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Socket establishment Exception: {0}", e.getLocalizedMessage());
        }

    }

    /**
     * @return a NetworkPacket received over the network.
     */
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

    /**
     * It stops the thread and closes the communications politely. (Not like
     * selfDestruction.)
     */
    public void endCommunications() {

        communicatorIsNeeded = false;

    }

    /**
     * @return A string list of game names found in the local network.
     */
    public String[] getAvailableGames() {

        String[] a = new String[]{null};
        return availableGames.keySet().toArray(a);

    }

    /**
     * Connects to a server game on the local network
     *
     * @param gameName The name of the LAN game
     * @return whether the connection was created successfully
     */
    public boolean connectToGame(String gameName) {

        LOGGER.log(Level.FINE, "Connecting to game: {0}", gameName);

        if (availableGames.containsKey(gameName)) {

            // finish the search
            stopSearchingForGames();
            // get the server address
            partnerAddress = availableGames.get(gameName).getServerIPAddress();
            // get the server port
            partnerPort = availableGames.get(gameName).getServerPortNumber();
            // connect to game
            connectToServer();
        }

        return false;

    }

    /**
     * Stops the search for the server games on the LAN.
     */
    private void stopSearchingForGames() {
        needToSearchForGames = false;
        LOGGER.log(Level.FINE, "Searching for games has stopped.");
    }

    /**
     * Starts searching for server games on the LAN.
     */
    private void searchForGames() {

        MulticastSocket ms = null;
        InetAddress group = null;
        try {
            ms = new MulticastSocket(60005);
            group = InetAddress.getByName("225.0.0.1");
            ms.joinGroup(group);

            DatagramPacket packet;

            byte[] buf = new byte[1024];
            packet = new DatagramPacket(buf, buf.length);

            while (needToSearchForGames) {
                ms.receive(packet);
                NetworkPacket recivedPacket = deserialisePacket(buf);
                String gameName = ((GameInfo) recivedPacket.getInfo()).getGameName();

                // if a new game is detected, than add to the queue
                if (!availableGames.containsKey(gameName)) {
                    GameInfo gameInfo = (GameInfo) recivedPacket.getInfo();
                    gameInfo.setServerIPAddress(packet.getAddress());
                    availableGames.put(gameName, gameInfo);
                    timeoutOfTheGames.put(gameName, System.currentTimeMillis());
                    LOGGER.log(Level.INFO, "Adding game to available games: {0} with address: {1} and port: {2}",
                            new Object[]{gameInfo.getGameName(), gameInfo.getServerIPAddress(), gameInfo.getServerPortNumber()});
                } else {
                    timeoutOfTheGames.put(gameName, new Long(System.currentTimeMillis()));
                    LOGGER.log(Level.FINEST, "Timeout time updated for game: {0}", gameName);
                }
                Iterator it = timeoutOfTheGames.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    if (System.currentTimeMillis() - ((Long) pairs.getValue()).longValue() > 5000) {
                        availableGames.remove((String) pairs.getKey());
                        it.remove();
                    }
                }
            }

            ms.leaveGroup(group);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Search for games exception: {0}", port);
        } finally {
            ms.close();
        }
    }

    /**
     * Selects a free port for the server, where it will be able to start
     * listening for client connection requests.
     */
    private void startListening() {

        // Create server socket, and wait for incoming connection
        for (; port < 61000; port++) {
            try {
                waitingServerSocket = new ServerSocket(port);
                LOGGER.log(Level.INFO, "Server is waiting for client on port: {0}", port);
                break;
            } catch (IOException ex) {
                LOGGER.log(Level.FINEST, "Port is closed: {0} : {1}", new Object[]{port, ex.getLocalizedMessage()});
                continue;
            }

        }

    }

    /**
     * Waits for the client to establish the TCP connection.
     */
    private void waitForClient() {

        // The IP Address of the communication partner
        // InetAddress partnerAddress = null;
        // The Server TCP communication port
        //int port = 60000; // ezt jo lenne randomizalni...

        try {

            connection = waitingServerSocket.accept();
            // Get the input and output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
        } catch (SocketException se) {
            LOGGER.log(Level.WARNING, "Socket Exception!: {0}", se);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Saiting on port exception: {0}", ex);
        } finally {
            try {
                waitingServerSocket.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Could not close serversocket: {0}", ex);
            }
        }

    }

    /**
     * The client connects to the server, with the previously set parameters.
     */
    private void connectToServer() {

        try {
            // Connect to server
            connection = new Socket(partnerAddress, partnerPort);
            // Get the input and output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Client could not establish socket connection: {0}", ex);
        }

    }

    /**
     * A separate thread, to advertise a server game on the LAN.
     */
    private class GameAdvertiser extends Thread {

        // the value can be set to false, to stop the GameAdvertiser
        private boolean needToAdvertise = true;

        /**
         * Advertises the server game in every 5 second.
         */
        @Override
        public void run() {

            LOGGER.log(Level.FINE, "New GameAdvertiser started...");

            DatagramSocket s = null;
            byte[] buf;
            DatagramPacket dp;

            try {

                int UDPPort = 60000;

                // getting the first available port...
                for (; UDPPort < 61000; UDPPort++) {
                    try {
                        // This port will be used, to send the advertise messages
                        s = new DatagramSocket(UDPPort);
                        LOGGER.log(Level.INFO, "Sending advertisement on port: {0}", UDPPort);
                        break;
                    } catch (IOException ex) {
                        LOGGER.log(Level.FINEST, "UDPPort is closed: {0} : {1}", new Object[]{port, ex.getLocalizedMessage()});
                        continue;
                    }

                }



                while (needToAdvertise) {

                    NetworkPacket np = new NetworkPacket(new GameInfo(controller.getGameName(), null, port)); // IP will be determined later...

                    buf = serialisePacket(np);

                    // Destination address and port
                    InetAddress group = InetAddress.getByName("225.0.0.1");
                    int groupPort = 60005;

                    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, groupPort);

                    LOGGER.log(Level.FINE, "Sending game info...: {0}", controller.getGameName());
                    s.send(packet);
                    try {
                        sleep(500);
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, "IOException in advertizement: {0}", ex.getLocalizedMessage());
                    }
                }

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "IOException in advertizement: {0}", ex.getLocalizedMessage());
            } finally {
                s.close();
            }
        }

        /**
         * Serializes a NetworkPacket
         *
         * @param packet the packet, which will be serialized
         * @return a byte array of the serialized packet
         */
        private byte[] serialisePacket(NetworkPacket packet) {
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

        /**
         * This function can be used to stop the GameAdvertiser
         */
        public void stopAdvertise() {
            needToAdvertise = false;
        }
    }

    /**
     * Deserializes a NetworkPacket
     * @param  a byte array of the serialized packet
     * @return a NetworkPacket, which is deserialized out of the bytes
     */
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
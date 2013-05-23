package Network;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * The GameInfo class is a serializable class, which is sent in the network during the game discovery phase.
 * @author Maria Buthi
 */
public class GameInfo implements Serializable {
    
    // The name of the game
    private String gameName;
    // The IP address of the Server
    private InetAddress serverIPAddress;
    // The port number, where the server is reachable
    private int serverPortNumber;

    /**
     * C'tor for the GameInfo
     * @param gameName the name of the game
     * @param serverIPAddress the IP address of the server
     * @param serverPortNumber the port number of the server
     */
    public GameInfo(String gameName, InetAddress serverIPAddress, int serverPortNumber) {
        this.gameName = gameName;
        this.serverIPAddress = serverIPAddress;
        this.serverPortNumber = serverPortNumber;
    }

    /**
     * Getter for the game name
     * @return the name of the game
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Getter for the IP address
     * @return the IP address of the server
     */
    public InetAddress getServerIPAddress() {
        return serverIPAddress;
    }

    /**
     * Getter for the port number
     * @return the port number of the server
     */
    public int getServerPortNumber() {
        return serverPortNumber;
    }

    /**
     * Setter for the Server IP address
     * @param serverIPAddress the IP address of the server (might be initialized only after the object was created).
     */
    public void setServerIPAddress(InetAddress serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }
    
}

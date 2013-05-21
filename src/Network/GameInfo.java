package Network;

import java.io.Serializable;
import java.net.InetAddress;

public class GameInfo implements Serializable {
    
    private String gameName;
    private InetAddress serverIPAddress;
    private int serverPortNumber;

    public GameInfo(String gameName, InetAddress serverIPAddress, int serverPortNumber) {
        this.gameName = gameName;
        this.serverIPAddress = serverIPAddress;
        this.serverPortNumber = serverPortNumber;
    }

    public String getGameName() {
        return gameName;
    }

    public InetAddress getServerIPAddress() {
        return serverIPAddress;
    }

    public int getServerPortNumber() {
        return serverPortNumber;
    }

    public void setServerIPAddress(InetAddress serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }
    
}

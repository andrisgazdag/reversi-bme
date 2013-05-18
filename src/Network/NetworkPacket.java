package Network;

import java.io.Serializable;

/**
 *
 * @author Robin
 */
public class NetworkPacket implements Serializable {

    private String gameName;
    
    public NetworkPacket(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }
}

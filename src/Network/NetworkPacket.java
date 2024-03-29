package Network;

import java.io.Serializable;

/**
 * A NetworkPacket is a serializable class which is sent through the LAN during
 * a multiplayer game.
 *
 * @author Maria Buthi
 */
public class NetworkPacket implements Serializable {

    // Content of the Packet
    private Object info;
    private static final long serialVersionUID = 11345174;

    /**
     * C'tor for the NP
     *
     * @param info
     */
    public NetworkPacket(Object info) {
        this.info = info;
    }

    /**
     * Getter for the content
     *
     * @return the content Object
     */
    public Object getInfo() {
        return info;
    }

    @Override
    public String toString() {

        if (info != null) {
            return info.toString();
        } else {
            return super.toString();
        }

    }
}

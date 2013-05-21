package Network;

import java.io.Serializable;

/**
 *
 * @author Robin
 */
public class NetworkPacket implements Serializable {

    private Object info;
    
    public NetworkPacket(Object info) {
        this.info = info;
    }

    public Object getInfo() {
        return info;
    }
}



import java.io.Serializable;

/**
   The Messages.RoutingInfo class
    To simplify a connection
 */
public class RoutingInfo implements Serializable {
    private int port;
    private String ip;
    public RoutingInfo(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}

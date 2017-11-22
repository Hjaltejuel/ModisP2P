import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
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

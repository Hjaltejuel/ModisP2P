import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
 */
public class JoinMessage implements Message, Serializable {
    String ip;
    int port;

    public JoinMessage(String ip, int port){
        this.ip= ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}

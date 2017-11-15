import java.io.Serializable;

/**
 * Created by Michelle on 11/15/2017.
 */
public class GetMessage implements Serializable
{
    private int key;
    private int port;
    private String ip;
    public GetMessage(int key, int port, String ip){
        this.key = key;
        this.port = port;
        this.ip = ip;
    }
    public int getKey()
    {
        return key;
    }
    public int getPort()
    {
        return port;
    }
    public String getIp()
    {
        return ip;
    }

}



import java.io.Serializable;

/**
 * The get message class
 * represents a getMessage to the node system
 */
public class GetMessage implements Message, Serializable
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

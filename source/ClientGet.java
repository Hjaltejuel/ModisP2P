import com.sun.xml.internal.ws.encoding.MtomCodec;

import java.io.*;
import java.net.Socket;

/**
 * Created by Michelle on 11/15/2017.
 */
public class ClientGet {
    String IP;
    int port;
    int key;
    Socket connectSocket;
    public ClientGet(String IP, int port, int key){
        connectSocket = null;
        this.IP = IP;
        this.port = port;
        this.key = key;
        Get();
    }
    private void Get(){
        GetMessage message = new GetMessage(key, port, IP);
        try{
            connectSocket = new Socket(IP, port);
            OutputStream out = connectSocket.getOutputStream();
            ObjectOutput s = new ObjectOutputStream(out);
            s.writeObject(message);
            s.flush();
            s.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

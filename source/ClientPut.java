

import java.io.*;
import java.net.Socket;

/**
 * Created by Michelle on 11/15/2017.
 */
public class ClientPut {
    static String IP;
    static int port;
    static int key;
    static String value;
    static Socket connectSocket;
    public static void main(String[] args){
        connectSocket = null;
        IP = args[0];
        port = Integer.parseInt(args[1]);
        key = Integer.parseInt(args[2]);
        value = args[3];
        put();
    }
    private static void put(){
        PutMessage putMessage = new PutMessage(key,value);
        try{
            System.out.println("Sending message from PutClient to ip: " + IP + " port: " + port + " key: " + key + " value: " + value);
            connectSocket = new Socket(IP, port);
            OutputStream out = connectSocket.getOutputStream();
            ObjectOutput s = new ObjectOutputStream(out);
            s.writeObject(putMessage);
            s.flush();
            s.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

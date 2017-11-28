
import java.io.*;
import java.net.Socket;

/**
 * The put client of this system
 * sends out a put message and terminates.
 * This could have been included in the node system, but for simplicity we choose to exclude it
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
        start();
    }
    private static void start(){
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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Michelle on 11/15/2017.
 */
public class ClientGet {
    static int port;
    static int key;
    static String ip;
    static PutMessage message = null;

    public static void main(String[] args){
        port = Integer.parseInt(args[1]);
        ip = args[0];
        key = Integer.parseInt(args[2]);
        get();
    }

    public static void get() {
        ServerSocket ser = null;
        Socket soc = null;
        try {
            ser = new ServerSocket(port);
        /*
         * This will wait for a connection to be made to this socket.
         */
            GetMessage getMessage = new GetMessage(key,port,ip);
            Socket socket = new Socket(ip, port);
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(getMessage);
            stream.flush();
            stream.close();

            soc = ser.accept();
            InputStream o = soc.getInputStream();
            ObjectInput s = new ObjectInputStream(o);
            message = (PutMessage) s.readObject();
            s.close();


            // print out what we just received
            System.out.println(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
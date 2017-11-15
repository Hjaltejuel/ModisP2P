import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Michelle on 11/15/2017.
 */
public class ClientPut {
    int port;
    String value;
    GetMessage message = null;
    public void ClientPut(int port, String value){
     this.port = port;
     this.value = value;
     put();
    }

    public void put() {
        ServerSocket ser = null;
        Socket soc = null;
        try {
            ser = new ServerSocket(port);
        /*
         * This will wait for a connection to be made to this socket.
         */
            soc = ser.accept();
            InputStream o = soc.getInputStream();
            ObjectInput s = new ObjectInputStream(o);
            message = (GetMessage) s.readObject();
            s.close();

            PutMessage pm = new PutMessage(message.getKey(), value);
            Socket socket = new Socket(message.getIp(), message.getPort());
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(pm);
            stream.flush();
            stream.close();

            // print out what we just received
            System.out.println(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
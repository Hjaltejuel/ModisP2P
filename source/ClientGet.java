
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * The get client
 * sends out get request to node p2p system and awaits answer in the form of a putMessage
 * This could have been included as a peer in the node system but for simplicity we have excluded it
 */
public class ClientGet {
    static int port;
    static int key;
    static String ip;
    static PutMessage message = null;
    static Random random = new Random();

    public static void main(String[] args){
        port = Integer.parseInt(args[1]);
        ip = args[0];
        key = Integer.parseInt(args[2]);
        start();
    }

    public static void start() {
        //establish sockets and initialize message
        ServerSocket ser = null;
        Socket soc = null;
        int portPersonal = random.nextInt(100)+8000;
        String ipPersonal = null;
        try {
            ipPersonal = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            ser = new ServerSocket(portPersonal);

            System.out.println("Sending message from GetClient to ip: " + ip + " port: " + port + " With ip: " + ipPersonal + " port: " + portPersonal);
            GetMessage getMessage = new GetMessage(key,portPersonal,ipPersonal);
            Socket socket = new Socket(ip, port);
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(getMessage);
            stream.flush();
            stream.close();

            // recieve message
            soc = ser.accept();
            InputStream o = soc.getInputStream();
            ObjectInput s = new ObjectInputStream(o);
            message = (PutMessage) s.readObject();
            System.out.println("Recieved message: " + message.getValue().toString());
            s.close();


            // print out what we just received
            System.out.println(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
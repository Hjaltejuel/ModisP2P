import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {
    private static String ip;
    private static int port;
    public static void main(String[] args){
        ip = args[0];
        port = Integer.parseInt(args[0]);
        Socket connectSocket = null;
        try {
            connectSocket = new Socket(ip, 7000);
            DataOutputStream outputStream = new DataOutputStream(connectSocket.getOutputStream());

        } catch (IOException e) {

        }
        while(true){

        }
    }
}

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
        try{
            connectSocket = new Socket("IP", port);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        System.out.println("Hello");
    }
}

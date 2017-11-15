import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {
    private static String coupledNodeIp;
    private static int coupledNodePort;
    private static int localPort;
    private static HashMap<Integer,String>  valueMap;

    public static void main(String[] args){
        localPort = Integer.parseInt(args[0]);
        if(args.length == 3) {
            coupledNodeIp = args[1];
            coupledNodePort = Integer.parseInt(args[2]);
        } else {
            coupledNodeIp = null;
            coupledNodePort = 0;
        }
        valueMap = new HashMap<>();

        inputHandling();


    }
    public static void inputHandling(){
        Thread inputHandler = new Thread(()->{
            ServerSocket inSocket = null;
            try {
                inSocket = new ServerSocket(localPort);
                while(true){
                    Socket received = inSocket.accept();
                    messageHandler(received);

                }
            } catch (IOException e) {
                System.out.println("Couldnt establish connection");
            }

        });
        inputHandler.start();

    }
    public static void messageHandler(Socket socket){
        Thread MessageHandler = new Thread(()->{
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message message = (Message) objectInputStream.readObject();
                if(message instanceof PutMessage){
                    putMessage((PutMessage) message);
                    inputStream.close();
                } else {
                    getMessage((GetMessage) message);
                    inputStream.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        MessageHandler.start();
    }
    public static void putMessage(PutMessage message){
        valueMap.put(message.getKey(),message.getValue());

    }
    public static void getMessage(GetMessage message) throws IOException {
        if(valueMap.containsKey(message.getKey())){
            Socket socket = new Socket(message.getIp(),message.getPort());
            PutMessage putMessage = new PutMessage(message.getKey(),valueMap.get(message.getKey()));
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(putMessage);
            stream.flush();
            stream.close();
        } else {
            if(coupledNodePort != 0 && coupledNodeIp!=null) {
                Socket socket = new Socket(coupledNodeIp, coupledNodePort);
                OutputStream out = socket.getOutputStream();
                ObjectOutput stream = new ObjectOutputStream(out);
                stream.writeObject(message);
                stream.flush();
                stream.close();
            }
        }
    }

}

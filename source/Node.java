import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {
    private static String coupledNodeForwardIp;
    private static int coupledNodeForwardPort;
    private static String coupledNodeBackwardIP;
    private static int coupledNodeBackwardPort;
    private static int localPort;
    private static HashMap<Integer,String>  valueMap;

    public static void main(String[] args){
        localPort = Integer.parseInt(args[0]);
        if(args.length == 3) {
            coupledNodeForwardIp = args[1];
            coupledNodeForwardPort = Integer.parseInt(args[2]);
            join(coupledNodeForwardIp,coupledNodeForwardPort);
        } else {
            coupledNodeForwardIp = null;
            coupledNodeForwardPort = 0;
        }
        valueMap = new HashMap<>();

        inputHandling();


    }

    public static void join(String ip, int port){
        System.out.println("Joining Node with port : " + localPort + " To Node with port : " + coupledNodeForwardPort);
        try {
            sendMessage(new JoinMessage(InetAddress.getLocalHost().getHostAddress(),localPort),ip,port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

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
                } else if ( message instanceof  GetMessage){
                    getMessage((GetMessage) message);
                    inputStream.close();
                } else if (message instanceof JoinMessage){

                    coupledNodeBackwardIP = ((JoinMessage) message).getIp();
                    coupledNodeBackwardPort = ((JoinMessage) message).getPort();

                    System.out.println("Recieved join from Node with port : " + coupledNodeBackwardPort);
                } else if (message instanceof  NodeMessage){
                    propagateNodeMessage((NodeMessage) message);
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
        System.out.println("Inserted message with value: " + message.getValue() + " key: " + message.getKey());

    }
    public static void getMessage(GetMessage message){
        if(valueMap.containsKey(message.getKey())){
            sendBackMessage(message);
        } else {
            propagateGetMessage(message);
        }
    }
    public static void propagateNodeMessage(NodeMessage message){
        if(valueMap.containsKey(message.getMessage().getKey())){
            sendBackMessage(message.getMessage());
        } else {
            if (message.forward == true && coupledNodeForwardIp != null) {
                System.out.println("Didnt find value proporgating foward to another node with port: " + coupledNodeForwardPort + " ip " + coupledNodeBackwardIP);
                sendMessage(message, coupledNodeForwardIp, coupledNodeForwardPort);
            } else {
                if (message.forward == false &&coupledNodeBackwardIP != null) {
                    System.out.println("Didnt find value proporgating backward to another node with port: " + coupledNodeBackwardPort + " ip " + coupledNodeBackwardIP);
                    sendMessage(message, coupledNodeBackwardIP, coupledNodeBackwardPort);
                }
            }
        }
        System.out.println("The callstack in the direction forward = " + message.getDirection() + " has ended");
    }
    public static void propagateGetMessage(GetMessage message){
        System.out.println("Didnt find value proporgating foward to another node with port: " + coupledNodeForwardPort + " ip " + coupledNodeBackwardIP);
        if(coupledNodeForwardIp!= null) {
            sendMessage(new NodeMessage(message, true), coupledNodeForwardIp, coupledNodeForwardPort);
        }
        if(coupledNodeBackwardIP!=null){
            System.out.println("Didnt find value proporgating backward to another node with port: " + coupledNodeBackwardPort + " ip " + coupledNodeBackwardIP);
            sendMessage(new NodeMessage(message,false),coupledNodeBackwardIP,coupledNodeBackwardPort);
        }


    }
    public static void sendBackMessage(GetMessage message){
        System.out.println("Sending back putmessage, value was found sending to port: " + message.getPort() + " ip: " +message.getIp());
        Socket socket = null;
        try {
            socket = new Socket(message.getIp(),message.getPort());
            PutMessage putMessage = new PutMessage(message.getKey(),valueMap.get(message.getKey()));
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(putMessage);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void sendMessage(Message message, String ip, int port){
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(message);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

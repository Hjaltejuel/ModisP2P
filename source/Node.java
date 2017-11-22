import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {

    private static RoutingInfo my;
    private static RoutingInfo next;
    private static RoutingInfo prev;
    private static RoutingInfo prevPrev;
    private static RoutingInfo first;

    private static HashMap<Integer,String>  valueMap;

    public static void main(String[] args){
        try {
            my = new RoutingInfo(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[0]));
            if(args.length == 3) {
                next = new RoutingInfo( args[1],Integer.parseInt(args[2]));
                join(next);
            } else {
                first = my;
                next = null;
            }
            valueMap = new HashMap<>();

            inputHandling();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



    }

    public static void join(RoutingInfo info){
        System.out.println("Joining Node with port : " + my.getPort() + " To Node with port : " + info.getPort());
        sendMessage(new JoinMessage(my),info);

    }
    public static void inputHandling(){
        Thread inputHandler = new Thread(()->{
            ServerSocket inSocket = null;
            try {
                inSocket = new ServerSocket(my.getPort());
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

                } else if(message instanceof JoinReplyMessage){
                    prev = ((JoinReplyMessage) message).getRouteInfo();
                    first = ((JoinReplyMessage) message).first;


                } else if (message instanceof JoinMessage){
                    if(((JoinMessage) message).visisted == 0) {
                        if (prev != null) {
                            sendMessage(new JoinReplyMessage(prev,first), ((JoinMessage) message).getRouteInfo());
                        } else {
                            sendMessage(new JoinReplyMessage(first,first), ((JoinMessage) message).getRouteInfo());
                        }
                        prev = ((JoinMessage) message).getRouteInfo();


                        if(next!=null){
                            ((JoinMessage) message).visisted = 1;
                            sendMessage(message,next);
                        }

                        System.out.println("Recieved join from Node with port : " + prev.getPort());
                    }
                    else {
                        prevPrev = ((JoinMessage) message).getRouteInfo();
                        System.out.println("SEEETING PREVPREV + " + prevPrev.getPort());
                    }

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
            if(message.getSenderID() != my.getPort()) {
                System.out.println("Didnt find value proporgating Backward to another node with port: " + prev.getPort() + " ip " + prev.getIp());
                sendMessage(message, prev);
            }
        } if(message.getSenderID() == my.getPort()) {
            System.out.println("The callstack has ended");
        }
    }
    public static void propagateGetMessage(GetMessage message){
        NodeMessage nodeMessage = new NodeMessage(message,my.getPort());
        System.out.println("Didnt find value proporgating Backward to another node with port: " + prev.getPort()+ " ip " + prev.getIp());
        sendMessage(nodeMessage,prev);



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
    public static void handleFailure(Message message){
                sendMessage(message,prevPrev);

    }
    public static void sendMessage(Message message, RoutingInfo routingInfo){
        Socket socket = null;
        try {
            socket = new Socket(routingInfo.getIp(), routingInfo.getPort());
            OutputStream out = socket.getOutputStream();
            ObjectOutput stream = new ObjectOutputStream(out);
            stream.writeObject(message);
            stream.flush();
            stream.close();
        } catch (ConnectException e) {
            handleFailure(message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

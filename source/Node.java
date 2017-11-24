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
    private static RoutingInfo nextNext;
    private static GUI gui;

    private static HashMap<Integer,String>  valueMap;

    public static void main(String[] args){

        try {
            my = new RoutingInfo(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[0]));
            if(args.length == 3) {
                next = new RoutingInfo( args[1],Integer.parseInt(args[2]));
                System.out.println("Next : " + next.getPort());
                join(next);
            } else {
                prev = null;
                next = null;
            }
            valueMap = new HashMap<>();

            inputHandling();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        gui = new GUI(my.getPort());
        gui.setTextOnJPanels(prev,next,nextNext);



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

                } else if ( message instanceof  GetMessage) {
                    getMessage((GetMessage) message);
                    inputStream.close();
                }else if (message instanceof  FailureMessage) {

                    //checking if there are only three nodes in the system, if there are 3 we set nextnext to null, and connect the node that send the failure info to prev and next
                    if(next.getPort()!=(((FailureMessage) message).getInfo().getPort())) {
                        System.out.println("Recieved failureMessage, restructing");
                        //set the prev to the non failing node
                        prev = ((FailureMessage) message).getInfo();
                        //send back to the failure node to set its nextNext to this nodes next
                        sendMessage(new SetNextNextMessage(next), prev);
                        System.out.println("Current Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: " + nextNext.getPort());
                        gui.setTextOnJPanels(prev,next,nextNext);
                    } else{
                        //set nextNext to null, and connect this to the previous
                        System.out.println("Recieved failureMessage, restructing, there were only 3 nodes in the system");
                        nextNext = null;
                        next = ((FailureMessage) message).getInfo();
                        prev = ((FailureMessage) message).getInfo();
                        System.out.println("Current Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: Null");
                        gui.setTextOnJPanels(prev,next,nextNext);
                    }

                } else if(message instanceof JoinReplyMessage) {
                    prev = ((JoinReplyMessage) message).getRouteInfo();
                    System.out.println("Recieved JoinReply Setting Prev to : " + ((JoinReplyMessage) message).getRouteInfo().getPort());
                    gui.setTextOnJPanels(prev,next,nextNext);

                } else if(message instanceof SetNextNextMessage){
                    nextNext = ((SetNextNextMessage) message).getNextNext();
                    System.out.println("Setting nextNext to : " + nextNext.getPort() );
                    gui.setTextOnJPanels(prev,next,nextNext);

                } else if (message instanceof JoinMessage){
                    //Hvis det er den første node der bliver connected til
                    if (next == null && prev == null) {
                        System.out.println("Connecting first Node and establishing connections");
                        next  = ((JoinMessage) message).getRouteInfo();
                        System.out.println("Next : " + next.getPort());
                        prev= ((JoinMessage) message).getRouteInfo();
                        System.out.println("Prev : " + prev.getPort());
                        sendMessage(new JoinReplyMessage(my), ((JoinMessage) message).getRouteInfo());
                        gui.setTextOnJPanels(prev,next,nextNext);
                    } else
                    //Recieved Join checking if it has been resend
                        if(((JoinMessage) message).visisted == 0) {
                            System.out.println("First Joinmessage recieved");

                            //If we are inserting in the middle of the ring
                            ((JoinMessage) message).visisted = 1;
                            sendMessage(new JoinReplyMessage(prev), ((JoinMessage) message).getRouteInfo());
                            sendMessage(new SetNextNextMessage(next),((JoinMessage) message).getRouteInfo());
                            sendMessage(message, prev);

                            prev = ((JoinMessage) message).getRouteInfo();
                            System.out.println("Setting prev : " + prev.getPort());
                            gui.setTextOnJPanels(prev,next,nextNext);

                        }
                         else  if (((JoinMessage) message).visisted == 1){
                            System.out.println("Recieved second joinmessage");
                            ((JoinMessage) message).visisted = 2;
                            nextNext = next;
                            next = ((JoinMessage) message).getRouteInfo();
                            System.out.println("NextNext : " + nextNext.getPort());
                            System.out.println("Next : " + next.getPort());
                            sendMessage(message,prev);
                            gui.setTextOnJPanels(prev,next,nextNext);
                        } else {
                            System.out.println("Recieved third joinmessage");
                                nextNext = ((JoinMessage) message).getRouteInfo();
                            System.out.println("NextNext : " + ((JoinMessage) message).getRouteInfo().getPort());
                            gui.setTextOnJPanels(prev,next,nextNext);

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
            System.out.println("Recieved getmessage, Found value, sending back");
        } else {
            propagateGetMessage(message);
        }
    }
    public static void propagateNodeMessage(NodeMessage message){
        if(valueMap.containsKey(message.getMessage().getKey())){
            sendBackMessage(message.getMessage());
        } else {
            if(message.getSenderID() != my.getPort()) {
                System.out.println("Didnt find value proporgating Foward to Next with port: " + next.getPort() + " ip " + next.getIp());
                sendMessage(message, next);
            }
        } if(message.getSenderID() == my.getPort()) {
            System.out.println("The callstack has ended no value was found");
        }
    }
    public static void propagateGetMessage(GetMessage message){
        NodeMessage nodeMessage = new NodeMessage(message,my.getPort());
        System.out.println("Didnt find value proporgating foward to Next with port: " + next.getPort()+ " ip " + next.getIp());
        sendMessage(nodeMessage,next);



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
    public static void handleFailure(Message message, RoutingInfo failureInfo){
        if(nextNext!= null) {
            System.out.println("Handling failure from Node with port : " + next.getPort() + " sending to nextNext with port: " + nextNext.getPort());
            sendMessage(message, nextNext);
            next = nextNext;
            nextNext = null;
            System.out.println("Current Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: Null");
            sendMessage(new FailureMessage(failureInfo, my), next);
            gui.setTextOnJPanels(prev,next,nextNext);
        } else {
            prev = null;
            next =null;
            nextNext = null;
            gui.setTextOnJPanels(prev,next,nextNext);
        }

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
            System.out.println("Found failure");
            handleFailure(message,routingInfo);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

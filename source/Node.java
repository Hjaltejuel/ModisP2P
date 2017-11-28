
import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {

    private static RoutingInfo my;
    private static RoutingInfo next;
    private static RoutingInfo prev;
    private static RoutingInfo nextNext;
    private static GUI gui;

    private static HashMap<Integer,String>  myMap;
    private static HashMap<Integer,String> nextMap;

    public static void main(String[] args){


        try {
            my = new RoutingInfo(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[0]));
            gui = new GUI(my.getPort());
            if(args.length == 3) {
                next = new RoutingInfo( args[1],Integer.parseInt(args[2]));
                sendMessage(new RequestUpdateValuesMessage(my),next);
                gui.append("Next : " + next.getPort());
                join(next);
            } else {
                prev = null;
                next = null;
            }
            myMap = new HashMap<>();
            nextMap = new HashMap<>();

            inputHandling();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        gui.setTextOnJPanels(prev,next,nextNext);



    }

    public static void join(RoutingInfo info){
        gui.append("Joining Program.Node with port : " + my.getPort() + " To Program.Node with port : " + info.getPort());
        
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
                gui.append("Couldnt establish connection");
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

                } else if ( message instanceof GetMessage) {
                    getMessage((GetMessage) message);
                    inputStream.close();
                } else if (message instanceof RequestUpdateValuesMessage){
                    sendMessage(new SendValuesMessage(myMap),((RequestUpdateValuesMessage) message).getToRoute());
                }
                else if (message instanceof  SendValuesMessage){
                    nextMap.putAll(((SendValuesMessage) message).getNext());

                }
                else  if(message instanceof SetValuesMessage){
                    myMap.put(((SetValuesMessage) message).getKey(),((SetValuesMessage) message).getVal());
                    showMapsInGui();


                    gui.append("Inserted message with value: " +((SetValuesMessage) message).getVal() + " key: " +((SetValuesMessage) message).getKey());
                }
                else if (message instanceof FailureMessage) {
                    //checking if there are only three nodes in the system, if there are 3 we set nextnext to null, and connect the node that send the failure info to prev and next
                    if(next.getPort()!=(((FailureMessage) message).getInfo().getPort())) {
                        gui.append("Recieved failureMessage, restructing");
                        //set the prev to the non failing node
                        prev = ((FailureMessage) message).getInfo();
                        //send back to the failure node to set its nextNext to this nodes next
                        sendMessage(new SetNextNextMessage(next), prev);
                        gui.append("Current Program.Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: " + nextNext.getPort());
                        gui.setTextOnJPanels(prev,next,nextNext);
                    } else{
                        //set nextNext to null, and connect this to the previous
                        gui.append("Recieved failureMessage, restructing, there were only 3 nodes in the system");
                        nextNext = null;
                        next = ((FailureMessage) message).getInfo();
                        prev = ((FailureMessage) message).getInfo();
                        gui.append("Current Program.Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: Null");
                        gui.setTextOnJPanels(prev,next,nextNext);
                    }

                } else if(message instanceof JoinReplyMessage) {
                    prev = ((JoinReplyMessage) message).getRouteInfo();
                    gui.append("Recieved JoinReply Setting Prev to : " + ((JoinReplyMessage) message).getRouteInfo().getPort());
                    gui.setTextOnJPanels(prev,next,nextNext);

                } else if(message instanceof SetNextNextMessage){
                    nextNext = ((SetNextNextMessage) message).getNextNext();
                    gui.append("Setting nextNext to : " + nextNext.getPort() );
                    gui.setTextOnJPanels(prev,next,nextNext);

                } else if (message instanceof JoinMessage){
                    //Hvis det er den første node der bliver connected til
                    if (next == null && prev == null) {
                        gui.append("Connecting first Program.Node and establishing connections");
                        next = ((JoinMessage) message).getRouteInfo();
                        gui.append("Next : " + next.getPort());
                        prev= ((JoinMessage) message).getRouteInfo();
                        gui.append("Prev : " + prev.getPort());
                        sendMessage(new JoinReplyMessage(my), ((JoinMessage) message).getRouteInfo());

                        sendMessage(new RequestUpdateValuesMessage(my),next);
                        gui.setTextOnJPanels(prev,next,nextNext);
                    } else
                    //Recieved Join checking if it has been resend
                        if(((JoinMessage) message).getVisisted()== 0) {
                            gui.append("First Joinmessage recieved");

                            //If we are inserting in the middle of the ring
                            ((JoinMessage) message).setVisisted(1);
                            sendMessage(new JoinReplyMessage(prev), ((JoinMessage) message).getRouteInfo());
                            sendMessage(new SetNextNextMessage(next),((JoinMessage) message).getRouteInfo());
                            sendMessage(message, prev);

                            prev = ((JoinMessage) message).getRouteInfo();
                            gui.append("Setting prev : " + prev.getPort());
                            gui.setTextOnJPanels(prev,next,nextNext);

                        }
                         else  if (((JoinMessage) message).getVisisted() == 1){
                            gui.append("Recieved second joinmessage");
                            ((JoinMessage) message).setVisisted(2);
                            nextNext = next;
                            next = ((JoinMessage) message).getRouteInfo();
                            gui.append("NextNext : " + nextNext.getPort());
                            gui.append("Next : " + next.getPort());
                            sendMessage(message,prev);
                            sendMessage(new RequestUpdateValuesMessage(my),next);
                            gui.setTextOnJPanels(prev,next,nextNext);
                        } else {
                            gui.append("Recieved third joinmessage");
                                nextNext = ((JoinMessage) message).getRouteInfo();
                            gui.append("NextNext : " + ((JoinMessage) message).getRouteInfo().getPort());
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
        if(next!= null) {
            nextMap.put(message.getKey(),message.getValue());
            sendMessage(new SetValuesMessage(message.getKey(), message.getValue()), next);
        } else {
            myMap.put(message.getKey(), message.getValue());
            showMapsInGui();
        }

    }

    public static void getMessage(GetMessage message){
        if(myMap.containsKey(message.getKey())){
            sendBackMessage(message);
            gui.append("Recieved getmessage, Found value, sending back");
        } else {
            propagateGetMessage(message);
        }
    }
    public static void propagateNodeMessage(NodeMessage message){
        if(myMap.containsKey(message.getMessage().getKey())){
            sendBackMessage(message.getMessage());
        } else {
            if(message.getSenderID() != my.getPort()) {
                gui.append("Didnt find value proporgating Foward to Next with port: " + next.getPort() + " ip " + next.getIp());
                sendMessage(message, next);
            }
        } if(message.getSenderID() == my.getPort()) {
            gui.append("The callstack has ended no value was found");
        }
    }
    public static void propagateGetMessage(GetMessage message){
        NodeMessage nodeMessage = new NodeMessage(message,my.getPort());
        gui.append("Didnt find value proporgating foward to Next with port: " + next.getPort()+ " ip " + next.getIp());
        sendMessage(nodeMessage,next);



    }
    public static void sendBackMessage(GetMessage message){
        gui.append("Sending back putmessage, value was found sending to port: " + message.getPort() + " ip: " +message.getIp());
        Socket socket = null;
        try {
            socket = new Socket(message.getIp(),message.getPort());
            PutMessage putMessage = new PutMessage(message.getKey(),myMap.get(message.getKey()));
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
        addValues();
        if(nextNext!= null) {
            gui.append("Handling failure from Program.Node with port : " + next.getPort() + " sending to nextNext with port: " + nextNext.getPort());
            if(message instanceof NodeMessage){
                if(myMap.containsKey(((NodeMessage) message).getMessage().getKey())){
                    sendBackMessage(((NodeMessage) message).getMessage());
                } else {
                    sendMessage(message, nextNext);
                }
            } else {
                sendMessage(message, nextNext);
            }
            next = nextNext;
            sendMessage(new SetNextNextMessage(next),prev);
            sendMessage(new RequestUpdateValuesMessage(my),next);
            nextNext = null;
            gui.append("Current Program.Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: Null");
            sendMessage(new FailureMessage(failureInfo, my), next);
            gui.setTextOnJPanels(prev,next,nextNext);

        } else {
            prev = null;
            next =null;
            nextNext = null;
            if(message instanceof  NodeMessage){
                if(myMap.containsKey(((NodeMessage) message).getMessage().getKey())) {
                    sendBackMessage(((NodeMessage) message).getMessage());
                }
            }
            if(message instanceof SetValuesMessage){
                myMap.put(((SetValuesMessage) message).getKey(),((SetValuesMessage) message).getVal());
                showMapsInGui();

            }
            gui.setTextOnJPanels(prev,next,nextNext);
        }


    }
    public static void addValues(){
        myMap.putAll(nextMap);
        showMapsInGui();
    }
    public static void showMapsInGui(){
        gui.appendValues(myMap);
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
            gui.append("Found failure");
            handleFailure(message,routingInfo);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 * Created by Michelle on 11/15/2017.
 */
public class Node {
    // The connections to other nodes
    private static RoutingInfo my;
    private static RoutingInfo next;
    private static RoutingInfo prev;
    private static RoutingInfo nextNext;
    //GUI element
    private static GUI gui;

    //Failure handling values, if a node fails add nextmap to mymap
    private static HashMap<Integer,String>  myMap;
    private static HashMap<Integer,String> nextMap;

    public static void main(String[] args){

        try {
            my = new RoutingInfo(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[0]));
            gui = new GUI(my.getPort());
            if(args.length == 3) {
                next = new RoutingInfo( args[1],Integer.parseInt(args[2]));
                //Asks the next node for its MyMap
                gui.append("Making call to get nextMap");
                sendMessage(new RequestUpdateValuesMessage(my),next);
                gui.append("Next : " + next.getPort());
                //Make the join call if there is a next
                join(next);
            } else {
                //If its the first node
                prev = null;
                next = null;
            }
            //initialize maps
            myMap = new HashMap<>();
            nextMap = new HashMap<>();
            //Start reciving messages
            inputHandling();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        gui.setTextOnJPanels(prev,next,nextNext);
    }

    public static void join(RoutingInfo info){

        gui.append("Joining this Node with port : " + my.getPort() + " To Node with port : " + info.getPort());
        //start copling
        sendMessage(new JoinMessage(my),info);

    }

    /**
     * The biggest method of the class, handles all incoming messages by creating a thread for each of them
     * This way the system can handle multiple request at the same time
     */
    public static void inputHandling(){
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
    }

    public static void messageHandler(Socket socket){
        Thread MessageHandler = new Thread(()->{
            InputStream inputStream = null;
            try {
                //recieve message
                inputStream = socket.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message message = (Message) objectInputStream.readObject();

                //The message was a putmessages and is therefore delegated to the putmessage method
                if(message instanceof PutMessage){
                    putMessage((PutMessage) message);

                //The message was a getMessage and there is delegated to the getMessage method
                } else if ( message instanceof GetMessage) {
                    getMessage((GetMessage) message);

                    //The message was a requestUdpateValuesMessage and therefore we send back myMap to the requester
                } else if (message instanceof RequestUpdateValuesMessage){
                    sendMessage(new SendValuesMessage(myMap),((RequestUpdateValuesMessage) message).getToRoute());
                    gui.append("MyMap has been requested sending it back");
                }

                //This node send a requestUpdateValuesMessage and now have recieved the nextMap
                else if (message instanceof  SendValuesMessage){
                    nextMap.putAll(((SendValuesMessage) message).getNext());
                    gui.append("Recieved NextMap");
                }
                //The message was a SetValueMessage and therefore we insert the value into myMap
                else  if(message instanceof SetValueMessage){
                    myMap.put(((SetValueMessage) message).getKey(),((SetValueMessage) message).getVal());
                    showMapsInGui();
                    gui.append("Inserted message with value: " +((SetValueMessage) message).getVal() + " key: " +((SetValueMessage) message).getKey());
                }

                //A Node has failed this is the nextNode to the node that discovered the failure
                else if (message instanceof FailureMessage) {

                    //checking if there are only three nodes in the system, if there are 3 we set nextnext to null, and connect the node that send the failure info to prev and next
                    if(next.getPort()!=(((FailureMessage) message).getInfo().getPort())) {
                        gui.append("Recieved failureMessage, restructing to be the nextNode of the sender");
                        //set the prev to the non failing node
                        prev = ((FailureMessage) message).getInfo();
                        //send back to the failure node to set its nextNext to this nodes next
                        sendMessage(new SetNextNextMessage(next), prev);

                        gui.setTextOnJPanels(prev,next,nextNext);
                        //if there are only 2 node now
                    } else{
                        //set nextNext to null, and connect this to the previous
                        gui.append("Recieved failureMessage, restructing, there were only 3 nodes in the system so nextNext is now null");
                        nextNext = null;
                        //Set the connections
                        next = ((FailureMessage) message).getInfo();
                        prev = ((FailureMessage) message).getInfo();

                        gui.setTextOnJPanels(prev,next,nextNext);
                    }

                    //The node recieved a JoinReply and can therefore set its prev
                } else if(message instanceof JoinReplyMessage) {
                    prev = ((JoinReplyMessage) message).getRouteInfo();
                    gui.append("Recieved JoinReply Setting Prev to : " + ((JoinReplyMessage) message).getRouteInfo().getPort());
                    gui.setTextOnJPanels(prev,next,nextNext);
                }

                //recieved a setNextNext call setting next next
                else if(message instanceof SetNextNextMessage){
                    nextNext = ((SetNextNextMessage) message).getNextNext();
                    gui.append("Recieved setNextNext Setting nextNext to : " + nextNext.getPort() );
                    gui.setTextOnJPanels(prev,next,nextNext);
                }

                //Recieved a message to join
                else if (message instanceof JoinMessage){
                    //If this is the only node in the system
                    if (next == null && prev == null) {
                        gui.append("Connecting first Node and establishing connections");
                        next = ((JoinMessage) message).getRouteInfo();
                        gui.append("Next : " + next.getPort());
                        prev= ((JoinMessage) message).getRouteInfo();
                        gui.append("Prev : " + prev.getPort());
                        //ask the other node to set its prev to my
                        sendMessage(new JoinReplyMessage(my), ((JoinMessage) message).getRouteInfo());
                        //ask the other node for its nextMap
                        sendMessage(new RequestUpdateValuesMessage(my),next);
                        gui.setTextOnJPanels(prev,next,nextNext);
                    } else
                         //if its the first node recieving a join
                        if(((JoinMessage) message).getVisisted()== 0) {
                            gui.append("First Joinmessage recieved");
                            ((JoinMessage) message).setVisisted(1);
                            //send a message to the new node to set its prev to this prev
                            sendMessage(new JoinReplyMessage(prev), ((JoinMessage) message).getRouteInfo());
                            //send a messsage to the new node to set its nextnext to this next
                            sendMessage(new SetNextNextMessage(next),((JoinMessage) message).getRouteInfo());
                            //send the JoinMessage backwards
                            sendMessage(message, prev);

                            prev = ((JoinMessage) message).getRouteInfo();
                            gui.append("Setting prev : " + prev.getPort());
                            gui.setTextOnJPanels(prev,next,nextNext);

                        }
                         else  if (((JoinMessage) message).getVisisted() == 1){
                            //if its the second node
                            gui.append("Recieved second joinmessage");
                            ((JoinMessage) message).setVisisted(2);
                            //set nextNext and next
                            nextNext = next;
                            next = ((JoinMessage) message).getRouteInfo();

                            gui.append("NextNext : " + nextNext.getPort());
                            gui.append("Next : " + next.getPort());
                            //proporgate once more
                            sendMessage(message,prev);
                            //ask for hashmap
                            sendMessage(new RequestUpdateValuesMessage(my),next);
                            gui.setTextOnJPanels(prev,next,nextNext);
                        } else {
                            //last message recieving
                            gui.append("Recieved third joinmessage");
                                nextNext = ((JoinMessage) message).getRouteInfo();
                            gui.append("NextNext : " + ((JoinMessage) message).getRouteInfo().getPort());
                            gui.setTextOnJPanels(prev,next,nextNext);
                        }
                } else if (message instanceof  NodeMessage){
                    propagateNodeMessage((NodeMessage) message);
                }
                inputStream.close();
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
            //If there is a next add it to nextMap and add it to next myMap
            nextMap.put(message.getKey(),message.getValue());
            sendMessage(new SetValueMessage(message.getKey(), message.getValue()), next);
        } else {
            //Add it to myMap
            myMap.put(message.getKey(), message.getValue());
            showMapsInGui();
        }

    }

    public static void getMessage(GetMessage message){
        //Check if Value is there else send onwards
        if(myMap.containsKey(message.getKey())){
            sendBackMessage(message);
            gui.append("Recieved getmessage, Found value, sending back");
        } else {
            propagateGetMessage(message);
        }
    }
    public static void propagateGetMessage(GetMessage message){
        //Change it to a node message
        NodeMessage nodeMessage = new NodeMessage(message,my.getPort());
        gui.append("Didnt find value proporgating foward to Next with port: " + next.getPort()+ " ip " + next.getIp());
        sendMessage(nodeMessage,next);
    }

    public static void propagateNodeMessage(NodeMessage message){
        //proporgate or send back
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

    public static void handleFailure(Message message, RoutingInfo failureInfo){
        //start by adding values
        addValues();
        //If there is any nodes left in the system
        if(nextNext!= null) {
            gui.append("Handling failure from Program.Node with port : " + next.getPort() + " sending to nextNext with port: " + nextNext.getPort());
            //If it is a node message we need to check if the value is now here after we added it
            if(message instanceof NodeMessage){
                if(myMap.containsKey(((NodeMessage) message).getMessage().getKey())){
                    sendBackMessage(((NodeMessage) message).getMessage());
                } else {
                    sendMessage(message, nextNext);
                }
            } else {
                sendMessage(message, nextNext);
            }
            //change connections
            next = nextNext;
            sendMessage(new SetNextNextMessage(next),prev);
            sendMessage(new SendValuesMessage(myMap),prev);
            sendMessage(new RequestUpdateValuesMessage(my),next);
            nextNext = null;
            gui.append("Current Program.Node connections Prev: " + prev.getPort() + " ------- My: " + my.getPort() + " ------- Next: " + next.getPort() + " ------- NextNext: Null");
            sendMessage(new FailureMessage(failureInfo, my), next);
            gui.setTextOnJPanels(prev,next,nextNext);

        } else {
            //if it endeed now is the only node
            prev = null;
            next =null;
            nextNext = null;
            //Check to see if it now contains
            if(message instanceof  NodeMessage){
                if(myMap.containsKey(((NodeMessage) message).getMessage().getKey())) {
                    sendBackMessage(((NodeMessage) message).getMessage());
                }
            }
            //check to see if we need to put a message to next then just add it here
            if(message instanceof SetValueMessage){
                myMap.put(((SetValueMessage) message).getKey(),((SetValueMessage) message).getVal());
                showMapsInGui();
            }
            gui.setTextOnJPanels(prev,next,nextNext);
        }


    }
    public static void addValues(){
        //if a failure has happened add all the nextMap to myMap
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

}


import java.io.Serializable;

/**
    The Messages.NodeMessage class
    Send when nodes wants to foward a get message between them
    If the senderID recieves a Messages.NodeMessage with its own id it terminates the call
 */
public class NodeMessage implements Message, Serializable{
    GetMessage message;
    int SenderID;

    public NodeMessage(GetMessage message, int senderID){

        this.message = message;
        this.SenderID = senderID;

    }

    public GetMessage getMessage(){
        return message;
    }
    public int getSenderID(){
        return SenderID;
    }



}

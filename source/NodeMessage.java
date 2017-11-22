import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
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

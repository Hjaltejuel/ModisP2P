import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
 */
public class NodeMessage implements Message, Serializable{
    GetMessage message;
    boolean forward;

    public NodeMessage(GetMessage message, boolean forward){

        this.message = message;
        this.forward = forward;
    }

    public GetMessage getMessage(){
        return message;
    }
    public boolean getDirection(){
        return forward;
    }
    public void setDirection(boolean direction){
        this.forward= direction;
    }


}

import java.io.Serializable;

/**
 * Created by Michelle on 23-11-2017.
 */
public class SetNextNextMessage implements Message, Serializable {
    RoutingInfo nextNext;

    public SetNextNextMessage(RoutingInfo nextNext){
        this.nextNext = nextNext;
    }
    public RoutingInfo getNextNext() {
        return nextNext;
    }
}

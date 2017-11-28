
import java.io.Serializable;

/**
   The setNextNextMessage class
    When a node recieves will set its nextnext
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

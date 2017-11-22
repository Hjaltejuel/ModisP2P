import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
 */
public class JoinReplyMessage extends JoinMessage {
    RoutingInfo first;

    public JoinReplyMessage(RoutingInfo route,RoutingInfo first){

        super(route);
        this.first = first;
    }
}

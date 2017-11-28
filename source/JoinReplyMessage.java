
/**
    The Messages.JoinReplyMessage class
    Send when a Program.Node recieves a Messages.JoinMessage

 */
public class JoinReplyMessage extends JoinMessage {


    public JoinReplyMessage(RoutingInfo route){

        super(route);
    }
}

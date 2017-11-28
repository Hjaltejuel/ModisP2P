

import java.io.Serializable;

/**
 * The failure message class
 * Messages.Message to relay to a nextnext to tell that a failure has happened.
 * Includes the failed nodes routinginfo and the sending nodes routing info
 */
public class FailureMessage implements Message, Serializable{
    private RoutingInfo sendingInfo;

    public FailureMessage(RoutingInfo failureInfo, RoutingInfo sendingInfo ){
        this.sendingInfo = sendingInfo;
    }


    public RoutingInfo getInfo() {
        return sendingInfo;
    }
}

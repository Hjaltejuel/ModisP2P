import java.io.Serializable;

/**
 * Created by Michelle on 23-11-2017.
 */
public class FailureMessage implements Message, Serializable{
    private RoutingInfo info;

    public FailureMessage(RoutingInfo failureInfo, RoutingInfo info ){
        this.info = info;
    }


    public RoutingInfo getInfo() {
        return info;
    }
}

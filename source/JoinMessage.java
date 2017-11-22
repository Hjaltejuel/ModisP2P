import java.io.Serializable;

/**
 * Created by Hjalte on 22-11-2017.
 */
public class JoinMessage implements Message, Serializable {
    RoutingInfo routeInfo;
    int visisted;

    public JoinMessage(RoutingInfo info){
        this.routeInfo = info;
        this.visisted = 0;
    }

    public RoutingInfo getRouteInfo() {
        return routeInfo;
    }
}

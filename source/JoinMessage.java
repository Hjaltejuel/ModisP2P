

import java.io.Serializable;

/**
  The Messages.JoinMessage class
  represents a join call to the node system
  The Messages.Message is initiated when a new Program.Node wants to join the system
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

    public int getVisisted() {
        return visisted;
    }

    public void setVisisted(int visisted) {
        this.visisted = visisted;
    }
}

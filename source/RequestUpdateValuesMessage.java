
import java.io.Serializable;
import java.util.HashMap;

/**
    The update Values message class
    This class is used when a node establish a next and therefore wants its hashmap for failure handling
 */
public class RequestUpdateValuesMessage implements Message, Serializable {
    RoutingInfo toRoute;

    public RequestUpdateValuesMessage(RoutingInfo toRoute){
        this.toRoute = toRoute;

    }

    public RoutingInfo getToRoute() {
        return toRoute;
    }
}


import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Hjalte on 28-11-2017.
 */
public class SendValuesMessage implements Message, Serializable {
    HashMap<Integer,String> next;

    public SendValuesMessage(HashMap<Integer,String> next){
        this.next = next;

    }

    public HashMap<Integer, String> getNext() {
        return next;
    }
}

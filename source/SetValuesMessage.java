
import java.io.Serializable;

/**
 * Created by Hjalte on 28-11-2017.
 */
public class SetValuesMessage implements Message, Serializable {
    int key;
    String val;
    public SetValuesMessage(int key, String val){
        this.key = key;
        this.val = val;
    }

    public int getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }
}


import java.io.Serializable;

/**
 * SetValueMessage class
 * Carries a single value
 */
public class SetValueMessage implements Message, Serializable {
    int key;
    String val;
    public SetValueMessage(int key, String val){
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

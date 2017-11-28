
import java.io.Serializable;

/**
    The Messages.PutMessage class
    The message that when a node recieves will input its key/val
 */
public class PutMessage implements Message, Serializable {
    private int key;
    private String value;
    public PutMessage(int key, String value)
    {
        this.key = key;
        this.value = value;
    }
    public int getKey()
    {
        return key;
    }
    public String getValue()
    {
        return value;
    }
}

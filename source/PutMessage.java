import java.io.Serializable;

/**
 * Created by Michelle on 11/15/2017.
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

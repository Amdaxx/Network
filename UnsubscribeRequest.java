import org.json.simple.JSONObject;

public class UnsubscribeRequest  extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            UnsubscribeRequest.class.getSimpleName();

    private String channelName;
    private String ID;
    // Constructor; throws NullPointerException if name is null.
    public UnsubscribeRequest(String ID, String channelName) {
        // check for null
        if (channelName == null)
            throw new NullPointerException();
        this.channelName = channelName;
        this.ID = ID;
    }

    String getChannelName() { return channelName; }
    String getID(){ return ID;}
    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("ID", ID);
        obj.put("channelName", channelName);


        return obj;
    }

    // Tries to deserialize a LoginRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static UnsubscribeRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String name = (String)obj.get("channelName");
            String ID = (String)obj.get("ID");
            // construct the object to return (checking for nulls)
            return new UnsubscribeRequest(ID, name);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
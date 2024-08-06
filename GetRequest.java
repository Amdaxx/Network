import org.json.simple.JSONObject;

public class GetRequest  extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            GetRequest.class.getSimpleName();

    private String ID;
    private long when;
    // Constructor; throws NullPointerException if message is null.
    public GetRequest(String ID, long when) {
        // check for null
        if (ID == null)
            throw new NullPointerException();
        this.ID = ID;
        this.when = when;
    }

    String getID(){return ID;}
    long getWhen(){return when;}
    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("ID", ID);
        obj.put("when", when);
        return obj;
    }

    // Tries to deserialize a PublishRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static GetRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            String ID = (String)obj.get("ID");
            long when = (long)obj.get("when");
            // construct the object to return (checking for nulls)
            return new GetRequest(ID, when);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
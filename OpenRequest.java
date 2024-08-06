import org.json.simple.*;

public class OpenRequest extends Request {
    private static final String _class =
            OpenRequest.class.getSimpleName();

    private String ID;

    // Constructor; throws NullPointerException if name is null.
    public OpenRequest(String ID) {
        // check for null
        if (ID == null)
            throw new NullPointerException();
        this.ID = ID;
    }

    String getID() { return ID; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("ID", ID);
        return obj;
    }

    // Tries to deserialize a LoginRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static OpenRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String ID = (String)obj.get("ID");
            // construct the object to return (checking for nulls)
            return new OpenRequest(ID);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}

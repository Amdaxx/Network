import org.json.simple.JSONObject;

public class LogRequest extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            LogRequest.class.getSimpleName();

    private String userIdentity;
    private String password;

    // Constructor; throws NullPointerException if name is null.
    public LogRequest(String userIdentity, String password) {
        // check for null
        if (userIdentity == null || password == null)
            throw new NullPointerException();
        this.userIdentity = userIdentity;
        this.password = password;
    }


    String getUserIdentity() {
        return userIdentity;
    }

    String getPassword() {
        return password;
    }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("userIdentity", userIdentity);
        obj.put("password", password);
        return obj;
    }

    // Tries to deserialize a LoginRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static LogRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject) val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize login name
            String userIdentity = (String) obj.get("userIdentity");
            String password = (String) obj.get("password");
            // construct the object to return (checking for nulls)
            return new LogRequest(userIdentity, password);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
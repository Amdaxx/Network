import org.json.simple.JSONObject;

public class PublishRequest  extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            PublishRequest.class.getSimpleName();

    private String message;
    private String ID;
    private Message msg;
    // Constructor; throws NullPointerException if message is null.
    public PublishRequest(String message, String ID, Message msg) {
        // check for null
        if (message == null)
            throw new NullPointerException();
        if (ID == null)
            throw new NullPointerException();
        this.message = message;
        this.ID = ID;
        this.msg = msg;
    }

    String getMessage(){return message;}
    Message getMsg() { return msg; }
    String getID(){return ID;}
    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("message", message);
        obj.put("_class", _class);
        obj.put("ID", ID);
        obj.put("msg", msg.toJSON());

        return obj;
    }

    // Tries to deserialize a PublishRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static PublishRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            String message = (String)obj.get("message");
            String ID = (String)obj.get("ID");
            Message msg = (Message.fromJSON(obj.get("msg")));

            return new PublishRequest(message, ID, msg);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
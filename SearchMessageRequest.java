import org.json.simple.JSONObject;

public class SearchMessageRequest  extends Request {
    // class name to be used as tag in JSON representation
    private static final String _class =
            SearchMessageRequest.class.getSimpleName();

    private String ID;
    private String word;
    // Constructor; throws NullPointerException if message is null.
    public SearchMessageRequest(String ID, String word) {
        // check for null
        if (ID == null)
            throw new NullPointerException();
        this.ID = ID;
        this.word= word;
    }

    String getID(){return ID;}
    String getWord(){return word;}
    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("ID", ID);
        obj.put("word", word);
        return obj;
    }

    // Tries to deserialize a PublishRequest instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static SearchMessageRequest fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize posted message
            String ID = (String)obj.get("ID");
            String word = (String)obj.get("word");
            // construct the object to return (checking for nulls)
            return new SearchMessageRequest(ID, word);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}
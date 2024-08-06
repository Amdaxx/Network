import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


import java.util.*;        // required for List and ArrayList
        import org.json.simple.*;  // required for JSON encoding and decoding

public class ChannelsResponse extends Response {
    // class name to be used as tag in JSON representation
    private static final String _class =
            ChannelsResponse.class.getSimpleName();

    private List<String> channels;

    // Constructor; throws NullPointerException if messages contains nulls.
    public ChannelsResponse(List<String> channels) {
        // check for nulls
        if (channels == null || channels.contains(null))
            throw new NullPointerException();
        this.channels = channels;
    }

    List<String> getChannels() { return channels; }

    // Serializes this object into a JSONObject
    @SuppressWarnings("unchecked")
    public Object toJSON() {
        // serialize messages into a JSONArray
        JSONArray arr = new JSONArray();
        for (String channel : channels)
            arr.add(channel);
        // serialize this as a JSONObject
        JSONObject obj = new JSONObject();
        obj.put("_class", _class);
        obj.put("channels", arr);
        return obj;
    }

    // Tries to deserialize a MessageListResponse instance from a JSONObject.
    // Returns null if deserialization was not successful (e.g. because a
    // different object was serialized).
    public static ChannelsResponse fromJSON(Object val) {
        try {
            JSONObject obj = (JSONObject)val;
            // check for _class field matching class name
            if (!_class.equals(obj.get("_class")))
                return null;
            // deserialize messages from JSONArray
            JSONArray arr = (JSONArray)obj.get("channels");
            List<String> channels = new ArrayList<>();
            for (Object msg_obj : arr)
                channels.add(msg_obj.toString());
            // construct the object to return (checking for nulls)
            return new ChannelsResponse(channels);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }
}

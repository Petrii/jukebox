package metropolia.edu.jukebox;

import com.google.android.gms.nearby.messages.Message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by petri on 1.5.2016.
 */
public class OfferBeacon {
    private static final String TYPE = "offer";

    public final String section;
    public final String offer;

    public OfferBeacon(Message message) {
        if (!TYPE.equals(message.getType())) {
            throw new IllegalArgumentException(
                    "Incorrect beacon message type: " + message.getType());
        }

        String json = new String(message.getContent());
        try {
            JSONObject parsed = new JSONObject(json);
            section = parsed.getString("section");
            offer = parsed.getString("latest_offer");
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON Contents");
        }
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof OfferBeacon
                && ((OfferBeacon) object).section.equals(section));
    }

    @Override
    public String toString() {
        return section;
    }
}

package abnormale.knochen.exquisitecode.web.messages;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement jsonType = jsonObject.get("type");
        if (jsonType == null) {
            return null;
        }
        String stype = jsonType.getAsString();
        for (Message.Type t : Message.Type.values()) {
            if (stype.equals(t.name())) {
                return jsonDeserializationContext.deserialize(jsonElement, t.getClazz());
            }
        }
        return null;
    }
}

package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.web.messages.Message;
import abnormale.knochen.exquisitecode.web.messages.client.ClientMessageDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Message.class, new ClientMessageDeserializer());
        gson = gsonBuilder.create();
    }

    public static String marshal(Object src) {
        return gson.toJson(src);
    }

    public static <T> T unmarshal(String src, Class<T> clazz) {
        return gson.fromJson(src, clazz);
    }
}

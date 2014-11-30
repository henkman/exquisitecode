package abnormale.knochen.exquisitecode.web;

import com.google.gson.Gson;

public class Json {
    private static Gson gson;

    static {
        gson = new Gson();
    }

    public static String marshal(Object src) {
        return gson.toJson(src);
    }

    public static <T> T unmarshal(String src, Class<T> clazz) {
        return gson.fromJson(src, clazz);
    }
}

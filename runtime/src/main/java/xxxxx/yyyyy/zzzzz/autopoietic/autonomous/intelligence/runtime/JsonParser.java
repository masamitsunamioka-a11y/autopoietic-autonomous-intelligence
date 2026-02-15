package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public interface JsonParser {
    <T> T from(String json, Type type);

    default <T> T from(String json) {
        return this.from(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    String to(Object object);
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public interface JsonParser {
    <T> T toObject(String json, Type type);

    default <T> T toObject(String json) {
        /// @formatter:off
        return this.toObject(json, new TypeToken<Map<String, Object>>() {}.getType());
        /// @formatter:on
    }

    String toString(Object object);
}

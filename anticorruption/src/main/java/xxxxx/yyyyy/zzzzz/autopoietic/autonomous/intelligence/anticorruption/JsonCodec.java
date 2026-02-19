package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public interface JsonCodec {
    <T> T unmarshal(String json, Type type);

    default <T> T unmarshal(String json) {
        /// @formatter:off
        return this.unmarshal(json, new TypeToken<Map<String, Object>>() {}.getType());
        /// @formatter:on
    }

    String marshal(Object object);
}

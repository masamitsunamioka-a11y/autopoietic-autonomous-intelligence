package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import java.lang.reflect.Type;
import java.util.Map;

public interface Serializer {
    <T> T deserialize(String data, Type type);

    default <T> T deserialize(String data) {
        return this.deserialize(data, Map.class);
    }

    String serialize(Object object);
}

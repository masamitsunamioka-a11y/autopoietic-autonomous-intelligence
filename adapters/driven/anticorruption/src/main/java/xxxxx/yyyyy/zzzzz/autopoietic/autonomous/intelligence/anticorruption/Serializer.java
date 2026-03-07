package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.lang.reflect.Type;

public interface Serializer {
    <T> T deserialize(String data, Type type);

    String serialize(Object object);
}

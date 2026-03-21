package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.lang.reflect.Type;

public interface Serializer {
    <T> T deserialize(String string, Type type);

    String serialize(Object object);
}

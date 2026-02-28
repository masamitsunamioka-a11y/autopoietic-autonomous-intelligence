package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working;

import java.util.Map;

public interface Memory {
    void encode(String key, Object value);

    Object retrieve(String key);

    /// [Engineering] Full state dump for prompt assembly; no Kandel equivalent.
    Map<String, Object> retrieve();

    void decay();
}

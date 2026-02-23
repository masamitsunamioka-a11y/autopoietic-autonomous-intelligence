package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Map;

public interface Memory {
    void record(String role, String text);

    Map<String, Object> conversation();

    void update(String key, Object value);

    Object lookup(String key);

    Map<String, Object> state();
}

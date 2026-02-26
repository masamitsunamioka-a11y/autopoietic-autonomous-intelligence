package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working;

import java.util.Map;

public interface State extends Memory {
    void update(String key, Object value);

    Object lookup(String key);

    Map<String, Object> state();
}

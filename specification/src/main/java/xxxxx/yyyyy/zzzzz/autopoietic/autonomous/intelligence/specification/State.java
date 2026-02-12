package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Map;

public interface State {
    void write(String key, Object value);

    Object read(String key);

    Map<String, Object> snapshot();
}

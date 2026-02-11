package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

import java.util.Map;

public interface State {
    void write(String key, Object value);

    Object read(String key);

    Map<String, Object> snapshot();
}

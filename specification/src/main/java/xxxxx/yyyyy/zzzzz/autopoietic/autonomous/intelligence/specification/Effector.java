package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Map;

/// Agentforce: Action
public interface Effector {
    String name();

    String label();

    String description();

    Map<String, Object> fire(Map<String, Object> input);
}

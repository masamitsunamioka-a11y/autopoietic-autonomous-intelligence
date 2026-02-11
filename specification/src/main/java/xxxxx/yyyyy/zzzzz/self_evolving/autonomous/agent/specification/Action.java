package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

import java.util.Map;

public interface Action<T extends Output> {
    default String name() {
        return this.getClass().getSimpleName();
    }

    String label();

    String description();

    T execute(Map<String, Object> input);
}

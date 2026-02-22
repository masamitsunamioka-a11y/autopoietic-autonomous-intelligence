package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Map;

public interface Action {
    String name();

    String label();

    String description();

    Map<String, Object> execute(Map<String, Object> input);
}

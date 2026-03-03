package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.Map;

public interface Effector extends Engravable {
    String tuning();

    /// [Engineering] As detailed in docs/kandel.md
    Map<String, Object> fire(Map<String, Object> input);
}

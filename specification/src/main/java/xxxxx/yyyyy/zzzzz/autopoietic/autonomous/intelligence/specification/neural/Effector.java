package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.util.Map;

public interface Effector extends AggregateRoot {
    String tuning();

    Map<String, Object> fire(Map<String, Object> input);
}

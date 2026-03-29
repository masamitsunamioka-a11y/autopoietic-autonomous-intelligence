package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.AggregateRoot;

import java.util.Map;

public interface Effector extends AggregateRoot {
    String program();

    Map<String, Object> fire(Map<String, Object> input);
}

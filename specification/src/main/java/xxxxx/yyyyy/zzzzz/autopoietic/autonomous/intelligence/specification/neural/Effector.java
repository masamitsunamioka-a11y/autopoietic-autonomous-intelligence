package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.Map;

public interface Effector extends Engram {
    String function();

    Map<String, Object> fire(Map<String, Object> input);
}

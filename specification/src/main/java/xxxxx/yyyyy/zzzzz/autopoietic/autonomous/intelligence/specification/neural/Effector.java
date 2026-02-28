package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural;

import java.util.Map;

public interface Effector extends Organ {
    String tuning();

    /// [Engineering] KV map models effector I/O; biology uses synaptic signals.
    Map<String, Object> fire(Map<String, Object> input);
}

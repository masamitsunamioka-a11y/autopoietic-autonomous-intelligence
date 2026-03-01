package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Nucleus {
    /// [Engineering] Class<T> = output type; biology has no explicit type dispatch.
    <T> T integrate(Impulse impulse, Class<T> type);
}

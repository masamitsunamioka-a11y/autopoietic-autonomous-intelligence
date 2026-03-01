package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Nucleus {
    /// [Engineering] Impulse = afferent signal; Class<?> caller = encoding phase dispatch; Class<T> = output type.
    <T> T integrate(Impulse impulse, Class<?> caller, Class<T> type);
}

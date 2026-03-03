package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Nucleus {
    /// [Engineering] As detailed in docs/kandel.md
    <T> T integrate(Impulse impulse, Class<T> response);
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Encoder {
    /// [Engineering] Class<?> caller dispatches encoding phase; no Kandel equivalent.
    String encode(Impulse impulse, Class<?> caller);
}

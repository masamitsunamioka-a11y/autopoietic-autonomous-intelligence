package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

/// [Engineering] As detailed in docs/kandel.md
public interface Encoder {
    /// [Engineering] As detailed in docs/kandel.md
    String encode(Impulse impulse, Class<?> caller);
}

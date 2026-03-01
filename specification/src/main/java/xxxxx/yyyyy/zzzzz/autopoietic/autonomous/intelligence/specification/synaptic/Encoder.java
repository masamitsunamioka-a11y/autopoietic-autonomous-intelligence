package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

/// [Engineering] Presynaptic encoding as a discrete step has no direct Kandel equivalent;
/// biology achieves this through vesicle release patterns (Ch.8-12).
public interface Encoder {
    /// [Engineering] Class<?> caller dispatches encoding phase.
    String encode(Impulse impulse, Class<?> caller);
}

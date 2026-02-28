package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Plasticity {
    /// [Engineering] Single Impulse triggers structural change; Kandel LTP requires
    /// repeated co-activation (Hebb's rule, NMDA receptor coincidence detection, Ch.63).
    void potentiate(Impulse impulse);

    void prune();
}

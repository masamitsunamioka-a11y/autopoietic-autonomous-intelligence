package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.integrative;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

/// Kandel Part III: Synaptic Transmission
public interface Transmitter {
    <T> T transmit(Impulse impulse, Class<T> response);
}

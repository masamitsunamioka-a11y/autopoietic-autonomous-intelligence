package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public record ImpulseImpl(
    Object signal,
    Mode mode,
    String direction) implements Impulse {
    public enum Mode {CEN, DMN}
}

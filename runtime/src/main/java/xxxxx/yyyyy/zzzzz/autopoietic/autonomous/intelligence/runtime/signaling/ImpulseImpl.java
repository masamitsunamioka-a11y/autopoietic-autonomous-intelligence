package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public record ImpulseImpl(
    Object signal,
    String afferent,
    String efferent,
    Mode mode) implements Impulse {
    public enum Mode {CEN, DMN}

    public ImpulseImpl(Object signal, Class<?> afferent,
                       String efferent, Mode mode) {
        this(signal,
            afferent.getName()
                .replaceAll(".*\\.|\\$.*", "")
                .replaceAll("Impl$", ""),
            efferent, mode);
    }
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public record ImpulseImpl(
    @NotBlank String signal,
    @NotBlank String afferent,
    String efferent,
    @NotNull Mode mode)
    implements Impulse {
    public enum Mode {
        CEN,
        DMN
    }

    /// fixme
    public ImpulseImpl(String signal, Class<?> afferent,
                       String efferent, Mode mode) {
        this(signal, afferent.getName()
                .replaceAll(".*\\.|\\$.*", "")
                .replaceAll("Impl$", ""),
            efferent, mode);
    }
}

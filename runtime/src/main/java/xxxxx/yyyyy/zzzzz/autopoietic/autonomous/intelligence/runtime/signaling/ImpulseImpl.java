package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public record ImpulseImpl(
    @NotBlank String signal,
    @NotBlank String afferent,
    String efferent
) implements Impulse {
}

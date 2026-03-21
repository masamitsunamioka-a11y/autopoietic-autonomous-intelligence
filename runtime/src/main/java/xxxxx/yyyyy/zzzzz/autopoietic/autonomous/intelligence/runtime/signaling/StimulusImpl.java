package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

public record StimulusImpl(
    @NotBlank String energy
) implements Stimulus {
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Decision(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String phase,
    String response,
    String effector,
    String target) {
    public Decision {
        switch (phase) {
            case "RESPOND" -> Objects.requireNonNull(response,
                "Response text is required for " + phase + " phase.");
            case "FIRE" -> Objects.requireNonNull(effector,
                "Effector name is required for " + phase + " phase.");
            case "PROPAGATE" -> Objects.requireNonNull(target,
                "Neuron name is required for " + phase + " phase.");
        }
    }
}

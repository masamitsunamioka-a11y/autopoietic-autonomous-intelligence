package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Signal(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String phase,
    String propagateTo,
    String effector,
    String answer) {
    public Signal {
        switch (phase) {
            case "FIRE" -> Objects.requireNonNull(effector,
                "Effector name is required for " + phase + " phase.");
            case "PROPAGATE" -> Objects.requireNonNull(propagateTo,
                "Neuron name is required for " + phase + " phase.");
            case "RESPOND" -> Objects.requireNonNull(answer,
                "Answer text is required for " + phase + " phase.");
        }
    }
}

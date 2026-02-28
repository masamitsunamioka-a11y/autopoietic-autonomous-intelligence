package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Decision(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String mode,
    String response,
    String effector,
    String area) {
    public Decision {
        switch (mode.toUpperCase()) {
            case "VOCALIZE", "INHIBIT" -> Objects.requireNonNull(response,
                "`response` is required for this mode.");
            case "FIRE" -> Objects.requireNonNull(effector,
                "`effector` is required for FIRE mode.");
            case "PROJECT" -> Objects.requireNonNull(area,
                "`area` is required for PROJECT mode.");
            default -> {
            }
        }
    }
}

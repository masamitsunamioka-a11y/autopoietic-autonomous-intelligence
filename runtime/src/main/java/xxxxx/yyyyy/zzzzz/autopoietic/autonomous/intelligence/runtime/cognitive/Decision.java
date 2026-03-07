package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Decision(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String process,
    String response,
    String effector,
    String area) {
    public Decision {
        process = process.toUpperCase();
        switch (process) {
            case "VOCALIZE", "INHIBIT" -> Objects.requireNonNull(
                response, "`response` is required for this process.");
            case "FIRE" -> Objects.requireNonNull(
                effector, "`effector` is required for FIRE process.");
            case "PROJECT" -> Objects.requireNonNull(
                area, "`area` is required for PROJECT process.");
            default -> {
            }
        }
    }
}

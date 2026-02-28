package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.drive;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Urge(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    boolean aroused,
    boolean vocalize,
    String area,
    String signal) {
    public Urge {
        if (aroused) {
            Objects.requireNonNull(area, "Area is required when aroused is true.");
            Objects.requireNonNull(signal, "Signal is required when aroused is true.");
        }
    }
}

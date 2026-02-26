package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.drive;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Urge(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    boolean aroused,
    boolean vocalize,
    String ideation) {
    public Urge {
        if (aroused) {
            Objects.requireNonNull(ideation, "Ideation is required when aroused is true.");
        }
    }
}

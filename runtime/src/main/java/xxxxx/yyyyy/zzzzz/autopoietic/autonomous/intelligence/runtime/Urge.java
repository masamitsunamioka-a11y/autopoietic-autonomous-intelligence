package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record Urge(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    boolean shouldFire,
    String answer) {
    public Urge {
        if (shouldFire) {
            Objects.requireNonNull(answer, "Answer is required when shouldFire is true.");
        }
    }
}

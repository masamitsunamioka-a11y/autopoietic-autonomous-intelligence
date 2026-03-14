package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.Objects;

public record Fluctuation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    boolean aroused,
    String area,
    String signal)
    implements Potential {
    public Fluctuation {
        if (aroused) {
            Objects.requireNonNull(
                area, "Area is required when aroused is true.");
            Objects.requireNonNull(
                signal, "Signal is required when aroused is true.");
        }
    }
}

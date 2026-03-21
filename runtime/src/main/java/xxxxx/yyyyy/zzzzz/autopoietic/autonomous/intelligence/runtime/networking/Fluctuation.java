package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

public record Fluctuation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double amplitude,
    boolean aroused,
    String signal
) implements Potential {
}

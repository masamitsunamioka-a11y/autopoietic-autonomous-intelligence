package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.networking;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

public record Spindle(
    @DecimalMin("0.0") @DecimalMax("1.0") double amplitude
) implements Potential {
}

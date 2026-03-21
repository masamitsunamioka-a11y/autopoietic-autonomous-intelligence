package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

public record Decision(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double amplitude,
    @NotBlank String process,
    String response,
    String effector,
    String area
) implements Potential {
}

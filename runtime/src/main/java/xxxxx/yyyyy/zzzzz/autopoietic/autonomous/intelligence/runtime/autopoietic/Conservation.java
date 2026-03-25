package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.synaptic.Potential;

import java.util.List;

public record Conservation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double amplitude,
    @NotNull List<@Valid NewArea> newAreas,
    @NotNull List<@Valid NewNeuron> newNeurons,
    @NotNull List<String> obsoleteAreas,
    @NotNull List<String> obsoleteNeurons
) implements Potential {
}

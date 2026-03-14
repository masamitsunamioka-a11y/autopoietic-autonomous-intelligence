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
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotNull List<@Valid MergedArea> mergedAreas,
    @NotNull List<@Valid MergedNeuron> mergedNeurons)
    implements Potential {
    public record MergedArea(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid NewArea newArea) {
    }

    public record MergedNeuron(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid NewNeuron newNeuron) {
    }
}

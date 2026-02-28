package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.plasticity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Pruning(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotNull List<@Valid MergedArea> mergedAreas,
    @NotNull List<@Valid MergedNeuron> mergedNeurons) {
    public record MergedArea(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Potentiation.Area result) {
    }

    public record MergedNeuron(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Potentiation.Neuron result) {
    }
}

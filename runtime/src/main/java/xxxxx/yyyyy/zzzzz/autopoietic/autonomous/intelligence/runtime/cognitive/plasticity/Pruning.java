package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.plasticity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;

import java.util.List;

public record Pruning(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotNull List<@Valid MergedNeuron> mergedNeurons,
    @NotNull List<@Valid MergedModule> mergedModules) {
    public record MergedNeuron(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Neuron result) {
    }

    public record MergedModule(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Module result) {
    }

    public record Neuron(
        @NotBlank String name,
        @NotBlank String function,
        @NotBlank String disposition,
        @NotNull List<String> modules) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name;}
        /// @formatter:on
    }

    public record Module(
        @NotBlank String name,
        @NotBlank String function,
        @NotBlank String disposition,
        @NotNull List<String> effectors) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name;}
        /// @formatter:on
    }
}

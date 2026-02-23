package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Pruning(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotNull List<@Valid MergedNeuron> mergedNeurons,
    @NotNull List<@Valid MergedSchema> mergedSchemas) {
    public record MergedNeuron(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Neuron result) {
    }

    public record MergedSchema(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid Schema result) {
    }

    public record Neuron(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String protocol,
        @NotNull List<String> schemas) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name;}
        /// @formatter:on
    }

    public record Schema(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String protocol,
        @NotNull List<String> effectors) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name;}
        /// @formatter:on
    }
}

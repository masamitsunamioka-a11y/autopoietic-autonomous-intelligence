package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.plasticity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;

import java.util.List;

public record Potentiation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String newDisposition,
    @NotNull List<@Valid Neuron> newNeurons,
    @NotNull List<@Valid Module> newModules,
    @NotNull List<@Valid Effector> newEffectors) {
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

    public record Effector(
        @NotBlank String name,
        @NotBlank String function,
        @NotBlank String execution,
        @NotNull List<String> modules) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name;}
        /// @formatter:on
    }
}

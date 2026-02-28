package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.homeostatic.plasticity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Engram;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Organ;

import java.util.List;

public record Potentiation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String newTuning,
    @NotNull List<@Valid Area> newAreas,
    @NotNull List<@Valid Neuron> newNeurons,
    @NotNull List<@Valid Effector> newEffectors) {
    public record Area(
        @NotBlank String name,
        @NotBlank String tuning,
        @NotNull List<String> neurons,
        @NotNull List<String> effectors) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name; }
        /// @formatter:on
    }

    public record Neuron(
        @NotBlank String name,
        @NotBlank String tuning) implements Engram {
        /// @formatter:off
        @Override public String name() { return this.name; }
        /// @formatter:on
    }

    public record Effector(
        @NotBlank String name,
        @NotBlank String tuning,
        @NotBlank String execution,
        @NotNull List<String> areas) implements Organ {
        /// @formatter:off
        @Override public String name() { return this.name; }
        /// @formatter:on
    }
}

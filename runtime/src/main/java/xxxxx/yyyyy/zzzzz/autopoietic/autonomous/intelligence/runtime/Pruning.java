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
    @NotNull List<@Valid MergedReceptor> mergedReceptors) {
    public record MergedNeuron(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid NeuronDefinition result) {
    }

    public record MergedReceptor(
        @NotNull List<String> sources,
        @NotBlank String reasoning,
        @NotNull @Valid ReceptorDefinition result) {
    }

    public record NeuronDefinition(
        @NotBlank String name,
        @NotBlank String label,
        @NotBlank String description,
        @NotBlank String instructions,
        @NotNull List<String> receptors,
        @NotBlank String rawJson) implements Storable {
        @Override
        public String serialize() {
            return this.rawJson;
        }
    }

    public record ReceptorDefinition(
        @NotBlank String name,
        @NotBlank String label,
        @NotBlank String description,
        @NotBlank String instructions,
        @NotNull List<String> effectors,
        @NotBlank String rawJson) implements Storable {
        @Override
        public String serialize() {
            return this.rawJson;
        }
    }
}

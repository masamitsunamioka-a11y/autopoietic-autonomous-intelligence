package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Potentiation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String newInstructions,
    @NotNull List<@Valid NeuronDefinition> newNeurons,
    @NotNull List<@Valid ReceptorDefinition> newReceptors,
    @NotNull List<@Valid EffectorDefinition> newEffectors) {
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

    public record EffectorDefinition(
        @NotBlank String name,
        @NotBlank String label,
        @NotBlank String description,
        @NotBlank String execution,
        @NotNull List<String> relatedReceptors,
        @NotBlank String rawJson) implements Storable {
        @Override
        public String serialize() {
            return this.rawJson;
        }
    }
}

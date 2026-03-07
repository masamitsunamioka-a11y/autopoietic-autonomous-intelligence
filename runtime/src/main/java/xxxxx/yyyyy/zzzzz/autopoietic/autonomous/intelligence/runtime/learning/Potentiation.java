package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record Potentiation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String newTuning,
    @NotNull List<@Valid Area> newAreas,
    @NotNull List<@Valid Neuron> newNeurons,
    @NotNull List<@Valid Effector> newEffectors) {
    public record Area(
        @NotBlank String id,
        @NotBlank String tuning,
        @NotNull List<String> neurons,
        @NotNull List<String> effectors)
        implements xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area {
    }

    public record Neuron(
        @NotBlank String id,
        @NotBlank String tuning)
        implements xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron {
    }

    public record Effector(
        @NotBlank String id,
        @NotBlank String tuning)
        implements xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector {
        @Override
        public Map<String, Object> fire(Map<String, Object> input) {
            throw new UnsupportedOperationException();
        }
    }
}

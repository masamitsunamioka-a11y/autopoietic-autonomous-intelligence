package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.learning;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Effector;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

import java.util.List;
import java.util.Map;

public record Potentiation(
    @NotBlank String reasoning,
    @DecimalMin("0.0") @DecimalMax("1.0") double confidence,
    @NotBlank String newTuning,
    @NotNull List<@Valid NewArea> newAreas,
    @NotNull List<@Valid NewNeuron> newNeurons,
    @NotNull List<@Valid NewEffector> newEffectors) {
    public record NewArea(
        @NotBlank String id,
        @NotBlank String tuning,
        @NotNull List<String> neurons,
        @NotNull List<String> effectors)
        implements Area {
    }

    public record NewNeuron(
        @NotBlank String id,
        @NotBlank String tuning)
        implements Neuron {
    }

    public record NewEffector(
        @NotBlank String id,
        @NotBlank String tuning)
        implements Effector {
        @Override
        public Map<String, Object> fire(
            Map<String, Object> input) {
            throw new UnsupportedOperationException();
        }
    }
}

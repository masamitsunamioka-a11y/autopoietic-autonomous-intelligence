package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.autopoietic;

import jakarta.validation.constraints.NotBlank;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;

public record NewNeuron(
    @NotBlank String id,
    @NotBlank String tuning
) implements Neuron {
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Neuron;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

public record StimulusImpl(
    String input,
    Neuron neuron) implements Stimulus {
}

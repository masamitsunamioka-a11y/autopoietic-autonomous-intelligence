package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Impulse;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;

public record ImpulseImpl(
    String input,
    Neuron neuron) implements Impulse {
}

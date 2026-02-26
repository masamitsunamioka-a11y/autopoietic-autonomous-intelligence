package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Percept;

public record InternalPercept(
    String neuron, String reasoning,
    double confidence, String answer) implements Percept {
}

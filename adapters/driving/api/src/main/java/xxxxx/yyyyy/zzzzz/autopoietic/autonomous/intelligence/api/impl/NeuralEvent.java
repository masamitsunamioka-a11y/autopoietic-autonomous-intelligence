package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record NeuralEvent(
    String type,
    Object content) implements Event {
    public NeuralEvent(Object content) {
        this("neural", content);
    }
}

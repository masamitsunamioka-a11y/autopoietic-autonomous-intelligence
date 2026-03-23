package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record StimulusFired(
    String type,
    String content
) implements Event {
    public StimulusFired(String content) {
        this("stimulus-fired", content);
    }
}

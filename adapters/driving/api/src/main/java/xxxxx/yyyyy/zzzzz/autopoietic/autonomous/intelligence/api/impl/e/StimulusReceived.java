package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record StimulusReceived(
    String type,
    String content
) implements Event {
    public StimulusReceived(String content) {
        this("stimulus-received", content);
    }
}

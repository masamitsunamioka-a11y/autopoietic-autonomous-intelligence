package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record ErrorEvent(
    String type,
    String content) implements Event {
    public ErrorEvent(String content) {
        this("error", content);
    }
}

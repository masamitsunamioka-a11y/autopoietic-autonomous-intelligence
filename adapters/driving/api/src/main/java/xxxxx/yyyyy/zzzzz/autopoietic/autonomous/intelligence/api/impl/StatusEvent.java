package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record StatusEvent(
    String type,
    String status) implements Event {
    public StatusEvent(String status) {
        this("status", status);
    }
}

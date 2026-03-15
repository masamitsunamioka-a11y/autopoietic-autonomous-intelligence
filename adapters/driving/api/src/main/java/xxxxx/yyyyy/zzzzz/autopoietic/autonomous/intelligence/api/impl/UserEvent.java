package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record UserEvent(
    String type,
    String content) implements Event {
    public UserEvent(String content) {
        this("user", content);
    }
}

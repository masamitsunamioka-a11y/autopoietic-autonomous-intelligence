package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record MessageEvent(
    String type,
    String location,
    String content) implements Event {
    public MessageEvent(String location, String content) {
        this("message", location, content);
    }
}

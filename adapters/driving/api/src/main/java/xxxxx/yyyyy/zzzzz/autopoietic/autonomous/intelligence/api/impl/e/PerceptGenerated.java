package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.impl.e;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api.Event;

public record PerceptGenerated(
    String type,
    String location,
    String content
) implements Event {
    public PerceptGenerated(String location, String content) {
        this("percept-generated", location, content);
    }
}

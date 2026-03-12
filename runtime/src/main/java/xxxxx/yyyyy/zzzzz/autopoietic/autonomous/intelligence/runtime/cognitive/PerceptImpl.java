package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

public record PerceptImpl(
    String content,
    String location,
    double intensity,
    long duration) implements Percept {
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

public record PerceptImpl(String content, String location) implements Percept {
    @Override
    public double intensity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double duration() {
        throw new UnsupportedOperationException();
    }
}

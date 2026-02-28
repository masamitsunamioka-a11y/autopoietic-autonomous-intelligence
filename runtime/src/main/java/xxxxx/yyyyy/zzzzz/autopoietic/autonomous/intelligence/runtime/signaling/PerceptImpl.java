package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.signaling;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.neural.Area;

public record PerceptImpl(
    Area area,
    String reasoning,
    double confidence,
    String content) implements Percept {
    @Override
    public String content() {
        return this.content;
    }

    @Override
    public String location() {
        return this.area.name();
    }

    @Override
    public double intensity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double duration() {
        throw new UnsupportedOperationException();
    }
}

package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive;

public interface Percept {
    String content();

    String location();

    double intensity();

    double duration();

    static Percept of(String content, String location) {
        record Instance(String content, String location) implements Percept {
            @Override
            public double intensity() {
                throw new UnsupportedOperationException();
            }

            @Override
            public double duration() {
                throw new UnsupportedOperationException();
            }
        }
        return new Instance(content, location);
    }
}

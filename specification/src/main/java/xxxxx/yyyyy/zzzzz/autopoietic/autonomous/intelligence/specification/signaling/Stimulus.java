package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling;

public interface Stimulus {
    String input();

    static Stimulus of(String input) {
        record Instance(String input) implements Stimulus {
        }
        return new Instance(input);
    }
}

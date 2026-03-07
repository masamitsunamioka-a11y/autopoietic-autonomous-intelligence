package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.homeostatic;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

public interface Salience {
    void orient();

    /// [Engineering] As detailed in docs/kandel.md
    void release(Percept percept);

    /// [Engineering] As detailed in docs/kandel.md
    boolean isOriented();

    /// [Engineering] As detailed in docs/kandel.md
    void await();
}

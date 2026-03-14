package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.networking;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;

public interface Salience {
    void orient();

    void release(Percept percept);

    boolean isOriented();

    void await();
}

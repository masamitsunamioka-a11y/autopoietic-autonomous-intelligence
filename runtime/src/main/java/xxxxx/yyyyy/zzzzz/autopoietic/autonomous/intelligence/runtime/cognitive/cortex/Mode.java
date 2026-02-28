package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive.cortex;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Impulse;

public interface Mode {
    Percept handle(Impulse impulse, Decision decision);
}

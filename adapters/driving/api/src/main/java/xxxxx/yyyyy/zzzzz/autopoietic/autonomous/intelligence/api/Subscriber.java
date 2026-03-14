package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.api;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.cognitive.Percept;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

public interface Subscriber {
    void onStimulus(Stimulus stimulus);

    void onPercept(Percept percept);
}

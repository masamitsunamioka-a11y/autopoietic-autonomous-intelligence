package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime.cognitive;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.signaling.Stimulus;

public interface Transducer {
    String perception(Stimulus stimulus);

    String relay(Stimulus stimulus);

    String potentiation(Stimulus stimulus);

    String pruning();

    String drive();
}

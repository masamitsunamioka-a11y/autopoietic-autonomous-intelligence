package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Neuron;

public interface PromptAssembler {
    String perception(Context context, Neuron self);

    String relay(Context context);

    String potentiation(Context context, Neuron self);

    String pruning();
}

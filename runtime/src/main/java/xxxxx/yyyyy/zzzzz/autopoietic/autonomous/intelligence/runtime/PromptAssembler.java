package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;

public interface PromptAssembler {
    String inference(Context context, Agent self);

    String routing(Context context);

    String upgrade(Context context, Agent self);

    String consolidation();
}

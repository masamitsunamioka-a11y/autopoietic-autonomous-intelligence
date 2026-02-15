package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Agent;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.State;

public interface PromptBuilder {
    String inference(String input, Conversation conversation, State state, Agent self);

    String routing(String input, Conversation conversation, State state);

    String upgrade(String input, Conversation conversation, State state, Agent self);

    String consolidation();
}

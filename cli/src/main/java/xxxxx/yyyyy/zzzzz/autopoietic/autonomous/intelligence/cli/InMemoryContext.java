package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.cli;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Context;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.State;

public record InMemoryContext(
    String input,
    Conversation conversation,
    State state) implements Context {
}

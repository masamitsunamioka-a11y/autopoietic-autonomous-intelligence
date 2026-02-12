package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.runtime;

import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.Conversation;
import xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.State;

public interface PromptBuilder {
    /// @formatter:off
    Session inference();
    Session routing();
    Session upgrade();
    Session consolidation();
    /// @formatter:on
    public interface Session {
        /// @formatter:off
        Session guardrails();
        Session input(String input);
        Session conversation(Conversation conversation);
        Session state(State state);
        Session agents();
        Session topics();
        Session actions();
        Session bind(String key, Object value);
        String render();
        /// @formatter:on
    }
}

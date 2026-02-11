package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.Conversation;
import xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification.State;

public interface PromptBuilder {
    /// @formatter:off
    Session reasoning();
    Session routing();
    Session evolution();
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

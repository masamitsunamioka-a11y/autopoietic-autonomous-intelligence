package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

public interface ReasoningEngine {
    Inference infer(String input, Conversation conversation, State state);
}

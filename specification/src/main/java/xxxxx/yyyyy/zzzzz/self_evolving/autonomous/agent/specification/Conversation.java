package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.specification;

import java.util.Map;

public interface Conversation {
    void write(String role, String text);

    Map<String, Object> snapshot();
}

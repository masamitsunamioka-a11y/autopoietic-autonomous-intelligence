package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Map;

public interface Conversation {
    void write(String role, String text);

    Map<String, Object> snapshot();
}

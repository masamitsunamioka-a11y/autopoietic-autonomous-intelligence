package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification.working;

import java.util.Map;

public interface Conversation extends Memory {
    void encode(String role, String text);

    Map<String, Object> conversation();
}

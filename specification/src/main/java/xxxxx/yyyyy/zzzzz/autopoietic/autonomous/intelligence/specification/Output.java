package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.specification;

import java.util.Collections;
import java.util.Map;

public interface Output {
    String message();

    default Map<String, Object> updates() {
        return Collections.emptyMap();
    }
}

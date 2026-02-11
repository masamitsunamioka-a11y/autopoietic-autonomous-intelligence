package xxxxx.yyyyy.zzzzz.self_evolving.autonomous.agent.runtime;

import java.util.Collection;

public class Util {
    public static boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static String cleanJson(String json) {
        if (json == null || json.isBlank()) {
            return "{}";
        }
        int start = json.indexOf("{");
        int end = json.lastIndexOf("}");
        if (start == -1 || end == -1 || start >= end) {
            return "{}";
        }
        return json.substring(start, end + 1).trim();
    }
}

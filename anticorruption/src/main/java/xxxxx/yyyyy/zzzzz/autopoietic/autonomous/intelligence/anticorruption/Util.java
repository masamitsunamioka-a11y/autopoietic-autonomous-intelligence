package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.util.Collection;

public class Util {
    public static boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static String toCamelCase(String input) {
        if (input == null || input.isBlank()) return input;
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    public static String toSnakeCase(String input) {
        if (input == null || input.isBlank()) return input;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) result.append('_');
                result.append(Character.toLowerCase(c));
            } else if (!Character.isLetterOrDigit(c)) {
                result.append('_');
            } else {
                result.append(c);
            }
        }
        return result.toString().replaceAll("_+", "_").replaceAll("^_+|_+$", "");
    }
}

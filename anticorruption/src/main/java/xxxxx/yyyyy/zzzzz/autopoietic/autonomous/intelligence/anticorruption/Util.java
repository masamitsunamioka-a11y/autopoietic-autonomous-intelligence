package xxxxx.yyyyy.zzzzz.autopoietic.autonomous.intelligence.anticorruption;

import java.lang.reflect.ParameterizedType;

public class Util {
    /// @SuppressWarnings("unchecked")
    public static <T> Class<T> actualTypeArguments(Class<?> c) {
        return (Class<T>) ((ParameterizedType) c.getGenericInterfaces()[0])
            .getActualTypeArguments()[0];
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
        return result.toString()
            .replaceAll("_+", "_")
            .replaceAll("^_+|_+$", "");
    }
}
